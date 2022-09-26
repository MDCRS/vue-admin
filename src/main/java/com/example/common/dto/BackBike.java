package com.example.common.dto;


import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class BackBike {

    private String defaultMsg;
    private Integer creditCount;
    private Double deposit;
    private String remark;
    private Long bikeRentId;
}
