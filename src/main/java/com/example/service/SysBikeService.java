package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.dto.RefundForm;
import com.example.common.lang.Result;
import com.example.entity.SysBike;

import java.io.IOException;
import java.util.List;

/**
 * @author 19846
 * @description 针对表【sys_bike】的数据库操作Service
 * @createDate 2022-04-23 17:38:53
 */
public interface SysBikeService extends IService<SysBike> {

    List<SysBike> getBikes(Integer rent_status, Integer bike_status);

    List<SysBike> getBikesOrderByRentCount(Integer rent_status, Integer bike_status);

    Result agreeRentBike(Integer bikeId, Integer type, String rentName) throws IOException;


    Result toRefund(Long bikeId, Long hostId, String rentName);

    Result refund(RefundForm refundForm);

    List<SysBike> getAll();

    Long getBrandCountByName(String s);

    Long[] getRentCountByName(String s);

    int getBikeSum();

    Result backDeposit(Long bikeRentId, Double deposit);
}
