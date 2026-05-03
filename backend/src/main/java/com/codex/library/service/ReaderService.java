package com.codex.library.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.codex.library.common.PageResult;
import com.codex.library.dto.reader.ReaderSaveRequest;
import com.codex.library.dto.reader.ReaderView;
import com.codex.library.entity.LoanRecord;
import com.codex.library.entity.ReaderProfile;
import com.codex.library.entity.Role;
import com.codex.library.entity.User;
import com.codex.library.exception.BusinessException;
import com.codex.library.mapper.LoanRecordMapper;
import com.codex.library.mapper.ReaderProfileMapper;
import com.codex.library.mapper.UserMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class ReaderService {

    private final ReaderProfileMapper readerProfileMapper;
    private final UserMapper userMapper;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final LoanRecordMapper loanRecordMapper;

    public ReaderService(ReaderProfileMapper readerProfileMapper,
                         UserMapper userMapper,
                         RoleService roleService,
                         PasswordEncoder passwordEncoder,
                         LoanRecordMapper loanRecordMapper) {
        this.readerProfileMapper = readerProfileMapper;
        this.userMapper = userMapper;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.loanRecordMapper = loanRecordMapper;
    }

    public PageResult<ReaderView> page(int current, int size, String keyword, String status) {
        Page<ReaderProfile> page = new Page<>(current, size);
        IPage<ReaderProfile> result = readerProfileMapper.selectPage(page, Wrappers.<ReaderProfile>lambdaQuery()
                .and(StringUtils.hasText(keyword), wrapper -> wrapper.like(ReaderProfile::getName, keyword)
                        .or()
                        .like(ReaderProfile::getReaderNo, keyword))
                .eq(StringUtils.hasText(status), ReaderProfile::getStatus, status)
                .orderByDesc(ReaderProfile::getCreatedAt));
        List<Long> userIds = result.getRecords().stream().map(ReaderProfile::getUserId).toList();
        Map<Long, User> userMap = userIds.isEmpty() ? Map.of() : userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        Map<Long, Long> currentLoanCountMap;
        if (result.getRecords().isEmpty()) {
            currentLoanCountMap = Map.of();
        } else {
            currentLoanCountMap = loanRecordMapper.selectList(Wrappers.<LoanRecord>lambdaQuery()
                            .in(LoanRecord::getReaderId, result.getRecords().stream().map(ReaderProfile::getId).toList())
                            .in(LoanRecord::getStatus, List.of("BORROWED", "OVERDUE")))
                    .stream()
                    .collect(Collectors.groupingBy(LoanRecord::getReaderId, Collectors.counting()));
        }

        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords().stream()
                .map(reader -> {
                    User user = userMap.get(reader.getUserId());
                    return new ReaderView(
                            reader.getId(),
                            reader.getUserId(),
                            reader.getReaderNo(),
                            user == null ? null : user.getUsername(),
                            reader.getName(),
                            reader.getPhone(),
                            reader.getEmail(),
                            reader.getIdCard(),
                            reader.getStatus(),
                            currentLoanCountMap.getOrDefault(reader.getId(), 0L)
                    );
                }).toList());
    }

    @Transactional
    public void create(ReaderSaveRequest request) {
        Role readerRole = roleService.findByCode("READER");
        if (readerRole == null) {
            throw new BusinessException("Reader role does not exist");
        }
        User user;
        if (request.userId() != null) {
            user = userMapper.selectById(request.userId());
            if (user == null) {
                throw new BusinessException("Linked user does not exist");
            }
            user.setRoleId(readerRole.getId());
            user.setDisplayName(request.name());
            user.setPhone(request.phone());
            user.setStatus(StringUtils.hasText(request.status()) ? request.status() : "ACTIVE");
            user.setUpdatedAt(LocalDateTime.now());
            userMapper.updateById(user);
        } else {
            if (!StringUtils.hasText(request.username()) || !StringUtils.hasText(request.password())) {
                throw new BusinessException("Username and password are required");
            }
            if (userMapper.findByUsername(request.username()) != null) {
                throw new BusinessException("Username already exists");
            }
            user = new User();
            user.setUsername(request.username());
            user.setPassword(passwordEncoder.encode(request.password()));
            user.setDisplayName(request.name());
            user.setRoleId(readerRole.getId());
            user.setPhone(request.phone());
            user.setStatus(StringUtils.hasText(request.status()) ? request.status() : "ACTIVE");
            user.setDeleted(0);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            userMapper.insert(user);
        }
        ReaderProfile readerProfile = new ReaderProfile();
        readerProfile.setUserId(user.getId());
        readerProfile.setReaderNo(generateReaderNo(user.getId()));
        readerProfile.setName(request.name());
        readerProfile.setPhone(request.phone());
        readerProfile.setEmail(request.email());
        readerProfile.setIdCard(request.idCard());
        readerProfile.setStatus(StringUtils.hasText(request.status()) ? request.status() : "ACTIVE");
        readerProfile.setDeleted(0);
        readerProfile.setCreatedAt(LocalDateTime.now());
        readerProfile.setUpdatedAt(LocalDateTime.now());
        readerProfileMapper.insert(readerProfile);
    }

    @Transactional
    public void update(Long id, ReaderSaveRequest request) {
        ReaderProfile readerProfile = readerProfileMapper.selectById(id);
        if (readerProfile == null) {
            throw new BusinessException("Reader does not exist");
        }
        User user = userMapper.selectById(readerProfile.getUserId());
        if (user == null) {
            throw new BusinessException("Reader account does not exist");
        }
        user.setDisplayName(request.name());
        user.setPhone(request.phone());
        user.setStatus(StringUtils.hasText(request.status()) ? request.status() : "ACTIVE");
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);

        readerProfile.setName(request.name());
        readerProfile.setPhone(request.phone());
        readerProfile.setEmail(request.email());
        readerProfile.setIdCard(request.idCard());
        readerProfile.setStatus(StringUtils.hasText(request.status()) ? request.status() : "ACTIVE");
        readerProfile.setUpdatedAt(LocalDateTime.now());
        readerProfileMapper.updateById(readerProfile);
    }

    @Transactional
    public void delete(Long id) {
        ReaderProfile readerProfile = readerProfileMapper.selectById(id);
        if (readerProfile == null) {
            throw new BusinessException("Reader does not exist");
        }
        long currentLoans = loanRecordMapper.selectCount(Wrappers.<LoanRecord>lambdaQuery()
                .eq(LoanRecord::getReaderId, id)
                .in(LoanRecord::getStatus, List.of("BORROWED", "OVERDUE")));
        if (currentLoans > 0) {
            throw new BusinessException("Reader has unreturned books");
        }
        readerProfileMapper.deleteById(id);
        userMapper.deleteById(readerProfile.getUserId());
    }

    public ReaderProfile findByUserId(Long userId) {
        return readerProfileMapper.findByUserId(userId);
    }

    public ReaderProfile getById(Long id) {
        return readerProfileMapper.selectById(id);
    }

    private String generateReaderNo(Long userId) {
        return "R" + String.format("%06d", userId);
    }
}
