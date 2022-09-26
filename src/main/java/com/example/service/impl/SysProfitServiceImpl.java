package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.SysProfit;
import com.example.service.SysProfitService;
import com.example.mapper.SysProfitMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author 19846
 * @description 针对表【sys_profit】的数据库操作Service实现
 * @createDate 2022-05-26 20:54:05
 */
@Service
public class SysProfitServiceImpl extends ServiceImpl<SysProfitMapper, SysProfit>
        implements SysProfitService {

    @Resource
    SysProfitMapper sysProfitMapper;

    @Override
    public double getProfit() {
        return sysProfitMapper.getProfit();
    }
}




