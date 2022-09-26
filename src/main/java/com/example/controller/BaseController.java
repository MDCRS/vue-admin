package com.example.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.mapper.SysAccountMapper;
import com.example.service.*;
import com.example.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;

public class BaseController {

    @Autowired
    HttpServletRequest request;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    SysUserService sysUserService;

    @Autowired
    SysRoleService sysRoleService;

    @Autowired
    SysMenuService sysMenuService;

    @Autowired
    SysUserRoleService sysUserRoleService;

    @Autowired
    SysRoleMenuService sysRoleMenuService;

    @Autowired
    SysBikeService sysBikeService;

    @Autowired
    SysRentBikeService sysRentBikeService;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    SysAccountService sysAccountService;


    public Page getPage() {
        int current = ServletRequestUtils.getIntParameter(request, "current", 1);
        int size = ServletRequestUtils.getIntParameter(request, "size", 4);
        return new Page(current, size);
    }
}
