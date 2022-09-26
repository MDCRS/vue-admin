package com.example.service;

import com.example.common.dto.BackBike;
import com.example.common.dto.PayBikeInfo;
import com.example.common.lang.Result;
import com.example.entity.AccountForm;
import com.example.entity.SysRentBike;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author 19846
 * @description 针对表【sys_rent_bike】的数据库操作Service
 * @createDate 2022-04-24 22:36:42
 */
public interface SysRentBikeService extends IService<SysRentBike> {

    Result toSaveRentBike(SysRentBike sysRentBike);

    Result calTime(Long rentBikeId, Double deposit, String endDate);

    Result backBike(BackBike backBike);

    Result payRent(PayBikeInfo payBikeInfo);

    List<SysRentBike> getAllRentBike();


    Long getBrandCountByName(String s);
}
