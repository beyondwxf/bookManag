package com.codex.library.controller;

import com.codex.library.common.ApiResponse;
import com.codex.library.dto.role.RoleView;
import com.codex.library.service.RoleService;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<RoleView>> list() {
        return ApiResponse.success(roleService.listAll());
    }
}
