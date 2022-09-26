package com.example.entity;

import lombok.Data;

@Data
public class AccountForm {

    private Long bikeId;
    private Long rentId;
    private Long hostId;
    private Integer payment;
    private Double totalPrice;
}
