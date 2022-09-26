package com.example.common.dto;


import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CheckUserDto {

    @NotNull
    private String username;

    @NotNull
    private String phone;

    @NotNull
    private String newPass;

    @NotNull
    private String code;

    private String key;
}
