package com.codex.library.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.codex.library.common.PageResult;
import com.codex.library.dto.user.UserCreateRequest;
import com.codex.library.dto.user.UserUpdateRequest;
import com.codex.library.dto.user.UserView;
import com.codex.library.entity.Role;
import com.codex.library.entity.User;
import com.codex.library.exception.BusinessException;
import com.codex.library.mapper.RoleMapper;
import com.codex.library.mapper.UserMapper;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class UserService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserMapper userMapper, RoleMapper roleMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public PageResult<UserView> page(int current, int size, String keyword, String status, Long roleId) {
        Page<User> page = new Page<>(current, size);
        IPage<User> result = userMapper.selectPage(page, Wrappers.<User>lambdaQuery()
                .and(StringUtils.hasText(keyword), wrapper -> wrapper.like(User::getUsername, keyword)
                        .or()
                        .like(User::getDisplayName, keyword))
                .eq(StringUtils.hasText(status), User::getStatus, status)
                .eq(roleId != null, User::getRoleId, roleId)
                .orderByDesc(User::getCreatedAt));
        Map<Long, Role> roleMap = roleMapper.selectList(Wrappers.<Role>lambdaQuery()).stream()
                .collect(Collectors.toMap(Role::getId, Function.identity()));
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords().stream()
                .map(user -> {
                    Role role = roleMap.get(user.getRoleId());
                    return new UserView(
                            user.getId(),
                            user.getUsername(),
                            user.getDisplayName(),
                            user.getRoleId(),
                            role == null ? null : role.getName(),
                            role == null ? null : role.getCode(),
                            user.getPhone(),
                            user.getStatus(),
                            user.getCreatedAt()
                    );
                }).toList());
    }

    @Transactional
    public void create(UserCreateRequest request) {
        if (userMapper.findByUsername(request.username()) != null) {
            throw new BusinessException("Username already exists");
        }
        if (roleMapper.selectById(request.roleId()) == null) {
            throw new BusinessException("Role does not exist");
        }
        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setDisplayName(request.displayName());
        user.setRoleId(request.roleId());
        user.setPhone(request.phone());
        user.setStatus(StringUtils.hasText(request.status()) ? request.status() : "ACTIVE");
        user.setDeleted(0);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(user);
    }

    @Transactional
    public void update(Long id, UserUpdateRequest request) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("User does not exist");
        }
        if (roleMapper.selectById(request.roleId()) == null) {
            throw new BusinessException("Role does not exist");
        }
        user.setDisplayName(request.displayName());
        user.setRoleId(request.roleId());
        user.setPhone(request.phone());
        user.setStatus(StringUtils.hasText(request.status()) ? request.status() : "ACTIVE");
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Transactional
    public void delete(Long id) {
        if (userMapper.selectById(id) == null) {
            throw new BusinessException("User does not exist");
        }
        userMapper.deleteById(id);
    }
}
