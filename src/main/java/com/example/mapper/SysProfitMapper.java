package com.example.mapper;

import com.example.entity.SysProfit;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @author 19846
 * @description 针对表【sys_profit】的数据库操作Mapper
 * @createDate 2022-05-26 20:54:05
 * @Entity com.example.entity.SysProfit
 */
public interface SysProfitMapper extends BaseMapper<SysProfit> {

    double getProfit();

}




