package com.codex.library.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.codex.library.dto.role.RoleView;
import com.codex.library.entity.Role;
import com.codex.library.mapper.RoleMapper;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    private final RoleMapper roleMapper;

    public RoleService(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    public List<RoleView> listAll() {
        return roleMapper.selectList(Wrappers.<Role>lambdaQuery().orderByAsc(Role::getId)).stream()
                .map(role -> new RoleView(role.getId(), role.getName(), role.getCode(), role.getDescription()))
                .toList();
    }

    public Role findByCode(String code) {
        return roleMapper.selectOne(Wrappers.<Role>lambdaQuery().eq(Role::getCode, code));
    }
}
