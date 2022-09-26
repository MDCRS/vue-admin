package com.example.service;

import com.example.entity.SysProfit;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author 19846
 * @description 针对表【sys_profit】的数据库操作Service
 * @createDate 2022-05-26 20:54:05
 */
public interface SysProfitService extends IService<SysProfit> {

    double getProfit();


}
