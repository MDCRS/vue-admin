package com.example.common.dto;


import lombok.Data;

@Data
public class RefundForm {
    private Long bikeId;
    private Double deposit;
    private Long hostId;
    private String rentName;
    private Double rentPrice;
}
