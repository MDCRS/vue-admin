package com.example.common.dto;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class SysPayRecordDto {
    private String username;
    private double payAmount;
    private double deposit;
    private LocalDateTime payTime;
    private String purpose;
}
