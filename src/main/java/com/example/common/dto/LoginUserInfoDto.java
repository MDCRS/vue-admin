package com.example.common.dto;

import com.example.entity.SysAccount;
import com.example.entity.SysLevel;
import com.example.entity.SysRole;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class LoginUserInfoDto {
    private Long id;
    private String username;
    private String avatar;
    private String phone;
    private Integer creditValue;
    private LocalDateTime lastLogin;
    private SysLevel sysLevel;
    private Integer status;
    private List<SysRole> roles = new ArrayList<>();
    private SysAccount sysAccount;
}
