package com.codex.library.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.codex.library.dto.auth.ChangePasswordRequest;
import com.codex.library.dto.auth.LoginRequest;
import com.codex.library.dto.auth.LoginResponse;
import com.codex.library.dto.auth.UserProfile;
import com.codex.library.entity.ReaderProfile;
import com.codex.library.entity.Role;
import com.codex.library.entity.User;
import com.codex.library.exception.BusinessException;
import com.codex.library.mapper.ReaderProfileMapper;
import com.codex.library.mapper.RoleMapper;
import com.codex.library.mapper.UserMapper;
import com.codex.library.security.CustomUserDetailsService;
import com.codex.library.security.JwtTokenProvider;
import com.codex.library.security.LoginUser;
import com.codex.library.security.SecurityContextHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final ReaderProfileMapper readerProfileMapper;

    public AuthService(CustomUserDetailsService customUserDetailsService,
                       JwtTokenProvider jwtTokenProvider,
                       PasswordEncoder passwordEncoder,
                       UserMapper userMapper,
                       RoleMapper roleMapper,
                       ReaderProfileMapper readerProfileMapper) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.readerProfileMapper = readerProfileMapper;
    }

    public LoginResponse login(LoginRequest request) {
        LoginUser loginUser = customUserDetailsService.loadByUsername(request.username());
        if (!"ACTIVE".equals(loginUser.getStatus())) {
            throw new BusinessException("账号已被禁用");
        }
        if (!passwordEncoder.matches(request.password(), loginUser.getPassword())) {
            throw new BusinessException("账号或密码错误");
        }
        String token = jwtTokenProvider.generateToken(loginUser.getUserId(), loginUser.getUsername(), loginUser.getRoleCode());
        Role role = roleMapper.selectById(loginUser.getRoleId());
        ReaderProfile readerProfile = readerProfileMapper.findByUserId(loginUser.getUserId());
        return new LoginResponse(token, new UserProfile(
                loginUser.getUserId(),
                loginUser.getUsername(),
                loginUser.getDisplayName(),
                loginUser.getRoleCode(),
                role == null ? null : role.getName(),
                readerProfile == null ? null : readerProfile.getId()
        ));
    }

    public UserProfile me() {
        LoginUser loginUser = SecurityContextHelper.currentUser();
        Role role = roleMapper.selectById(loginUser.getRoleId());
        ReaderProfile readerProfile = readerProfileMapper.findByUserId(loginUser.getUserId());
        return new UserProfile(
                loginUser.getUserId(),
                loginUser.getUsername(),
                loginUser.getDisplayName(),
                loginUser.getRoleCode(),
                role == null ? null : role.getName(),
                readerProfile == null ? null : readerProfile.getId()
        );
    }

    public void changePassword(ChangePasswordRequest request) {
        LoginUser loginUser = SecurityContextHelper.currentUser();
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getId, loginUser.getUserId()));
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new BusinessException("旧密码不正确");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userMapper.updateById(user);
    }
}
