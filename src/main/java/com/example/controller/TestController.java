package com.example.controller;

import com.example.common.lang.Result;
import com.example.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @Autowired
    SysUserService service;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/test")
    public Result test() {
        return Result.success(service.list());
    }

    @GetMapping("/test/pass")
    public Result pass() {
        String password = passwordEncoder.encode("1");
        boolean matches = passwordEncoder.matches("1", password);
        System.out.println("TestController->pass()->matches = " + matches);
        return Result.success(password);
    }

}
