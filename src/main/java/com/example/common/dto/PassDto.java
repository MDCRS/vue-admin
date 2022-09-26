package com.example.common.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PassDto {

    @NotBlank(message = "新密码不能为空")
    private String password;
    @NotBlank(message = "旧密码不能为空")
    private String CurrentPass;
}
