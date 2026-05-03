package com.codex.library.security;

import com.codex.library.entity.Role;
import com.codex.library.entity.User;
import com.codex.library.exception.BusinessException;
import com.codex.library.mapper.RoleMapper;
import com.codex.library.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;

    public CustomUserDetailsService(UserMapper userMapper, RoleMapper roleMapper) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
    }

    public LoginUser loadByUsername(String username) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new BusinessException("账号或密码错误");
        }
        Role role = roleMapper.selectById(user.getRoleId());
        if (role == null) {
            throw new BusinessException("用户角色不存在");
        }
        return new LoginUser(user.getId(), role.getId(), user.getUsername(), user.getPassword(), user.getDisplayName(), role.getCode(), user.getStatus());
    }

    public LoginUser loadById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        Role role = roleMapper.selectById(user.getRoleId());
        if (role == null) {
            throw new BusinessException("用户角色不存在");
        }
        return new LoginUser(user.getId(), role.getId(), user.getUsername(), user.getPassword(), user.getDisplayName(), role.getCode(), user.getStatus());
    }
}
