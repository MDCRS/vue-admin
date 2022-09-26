package com.example.common.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RentBikeDto {
    // image,hostName,brand,rentStatus,bikeStatus,startDate,endDate,returnDate
    private Long id;
    private String image;
    private String hostName;
    private String hostPhone;
    private String bikeName;
    private String brand;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime returnDate;
    private Double rentPrice;
    private Double deposit;
    private Integer agreement;
    private Integer isPay;
    private Integer isAgree;
}
