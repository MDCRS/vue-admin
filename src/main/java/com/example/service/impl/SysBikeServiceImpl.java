package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.dto.RefundForm;
import com.example.common.exception.SysEnum;
import com.example.common.exception.SysException;
import com.example.common.lang.Result;
import com.example.entity.*;
import com.example.mapper.*;
import com.example.service.SysBikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class SysBikeServiceImpl extends ServiceImpl<SysBikeMapper, SysBike> implements SysBikeService {


    @Autowired
    SysBikeMapper sysBikeMapper;

    @Resource
    private WebSocket webSocket;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private SysPayRecordMapper sysPayRecordMapper;

    @Resource
    private SysAccountMapper sysAccountMapper;

    @Resource
    private SysRentBikeMapper sysRentBikeMapper;

    @Override
    public List<SysBike> getBikes(Integer rent_status, Integer bike_status) {
        return sysBikeMapper.getBikes(rent_status, bike_status);
    }

    @Override
    public List<SysBike> getBikesOrderByRentCount(Integer rent_status, Integer bike_status) {
        return sysBikeMapper.getBikesOrderByRentCount(rent_status, bike_status);
    }

    @Override
    public Result agreeRentBike(Integer bikeId, Integer type, String rentName) {
        SysBike bike = sysBikeMapper.selectById(bikeId);
        SysUser hostUser = sysUserMapper.selectById(bike.getUserId());
        SysUser rentUser = sysUserMapper.selectOne(new QueryWrapper<SysUser>().eq("username", rentName));
        SysRentBike sysRentBike = sysRentBikeMapper.selectListByBikeIdAndHostIdAndRentId(bike.getId(), hostUser.getId(), rentUser.getId());

        String resStr = null;
        if (type == 1) {
            //同意出租
            //1。修改车辆相关字段
            bike.setIsAgree(type);
            bike.setUpdated(LocalDateTime.now());
            sysBikeMapper.updateById(bike);
            sysRentBike.setIsAgree(1);
            sysRentBikeMapper.updateById(sysRentBike);
            //2. 推送消息给客户端
            if (bike.getIsPay() == 0) {
                resStr = "车主:" + hostUser.getUsername() + "，以同意出租名为：" + bike.getBikeName() + "的车辆给您，请去支付后，去取车！";
            }
            if (bike.getIsPay() == 1) {
                resStr = "车主:" + hostUser.getUsername() + "，以同意出租名为：" + bike.getBikeName() + "的车辆给您，去取车吧！";
            }
            webSocket.sendMessage(resStr, bike.getRentName());
            //3.请求响应
            return Result.success("操作成功！");
        } else {
            //不同意出租
            bike.setIsAgree(type);
            bike.setRentStatus(1);
            bike.setRentPhone("");
            bike.setEndDate("");
            bike.setUpdated(LocalDateTime.now());
            sysRentBike.setIsAgree(0);
            String str;
            if (bike.getIsPay() == 0) {
                resStr = "车主:" + hostUser.getUsername() + "，不同意出租名为：" + bike.getBikeName() + "的车辆给您！";
                sysRentBike.setReturnDate(LocalDateTime.now());
                str = "操作成功！";
                webSocket.sendMessage(resStr, bike.getRentName());
                bike.setRentName("");
                sysBikeMapper.updateById(bike);
                sysRentBikeMapper.updateById(sysRentBike);
            } else {
                resStr = "车主:" + hostUser.getUsername() + "，不同意出租名为：" + bike.getBikeName() + "的车辆给您,正在退款,请注意查收！";
                webSocket.sendMessage(resStr, bike.getRentName());
                str = "操作失败，请先退款！";
            }
            return Result.success(str);
        }
    }

    @Override
    public Result toRefund(Long bikeId, Long hostId, String rentName) {
        List<SysPayRecord> list = sysPayRecordMapper.selectListByBikeIdAndHostId(bikeId, hostId);
        SysUser rentUser = sysUserMapper.selectOne(new QueryWrapper<SysUser>().eq("username", rentName));
        Map<String, Object> resMap = new HashMap<>();
        list.forEach(sysPayRecord -> {
            if (Objects.equals(sysPayRecord.getRentId(), rentUser.getId())) {
                Double rentPrice = sysPayRecord.getPayAmount();
                Double deposit = sysPayRecord.getDeposit();
                resMap.put("rentPrice", rentPrice);
                resMap.put("deposit", deposit);
            }
        });
        return Result.success(resMap);
    }

    @Override
    @Transactional
    public Result refund(RefundForm refundForm) {
        SysUser hostUser = sysUserMapper.selectById(refundForm.getHostId());
        SysUser rentUser = sysUserMapper.selectOne(new QueryWrapper<SysUser>().eq("username", refundForm.getRentName()));
        SysBike sysBike = sysBikeMapper.selectById(refundForm.getBikeId());

        SysRentBike sysRentBike = sysBikeMapper.selectByBikeIdAndHostIdAndRentId(sysBike.getId(), hostUser.getId(), rentUser.getId());

        SysAccount hostUserAccount = sysAccountMapper.selectByUserId(hostUser.getId());
        SysAccount rentUserAccount = sysAccountMapper.selectByUserId(rentUser.getId());

        Double sum = refundForm.getDeposit() + refundForm.getRentPrice();
        Map<String, Object> resMap = new HashMap<>();
        if (hostUserAccount.getBalance() < sum) {
            resMap.put("resCode", 2001);
            resMap.put("resMsg", "您的余额不足无法退款，请充值后再试！");
            return Result.success(resMap);
        }

        hostUserAccount.setBalance(hostUserAccount.getBalance() - sum);
        rentUserAccount.setBalance(rentUserAccount.getBalance() + sum);
        sysAccountMapper.updateById(hostUserAccount);
        sysAccountMapper.updateById(rentUserAccount);
        //退款车主给租户退款，收款方时租户，支出方式车主
        SysPayRecord sysPayRecord = new SysPayRecord();
        sysPayRecord.setPayAmount(refundForm.getRentPrice());
        sysPayRecord.setPayTime(LocalDateTime.now());
        sysPayRecord.setDeposit(refundForm.getDeposit());
        sysPayRecord.setCreated(LocalDateTime.now());
        sysPayRecord.setBikeId(sysBike.getId());
        sysPayRecord.setRentId(hostUser.getId());
        sysPayRecord.setHostId(rentUser.getId());
        sysPayRecord.setPurpose("退款");
        sysPayRecordMapper.insert(sysPayRecord);
        sysBike.setUpdated(LocalDateTime.now());
        sysBike.setIsPay(0);
        sysBikeMapper.updateById(sysBike);
        sysRentBike.setIsPay(0);
        sysRentBike.setUpdated(LocalDateTime.now());
        sysRentBike.setReturnDate(LocalDateTime.now());
        sysRentBikeMapper.updateById(sysRentBike);
        resMap.put("resCode", 2002);
        resMap.put("resMsg", "退款成功！");
        String msg = "车名为：" + sysBike.getBikeName() + "的车主，以把租金及押金退还给您,总金额为：" + sum;
        webSocket.sendMessage(msg, rentUser.getUsername());
        return Result.success(resMap);
    }

    @Override
    public List<SysBike> getAll() {
        return sysBikeMapper.getAll();
    }

    @Override
    public Long getBrandCountByName(String s) {
        return sysBikeMapper.getBrandCountByName(s);
    }

    @Override
    public Long[] getRentCountByName(String s) {
        return sysBikeMapper.getRentCountByName(s);
    }

    @Override
    public int getBikeSum() {
        return sysBikeMapper.getBikeSum();
    }

    @Override
    public Result backDeposit(Long bikeRentId, Double deposit) {
        //1.退还押金
        SysRentBike sysRentBike = sysRentBikeMapper.selectById(bikeRentId);
//        //查找要使用的信息
        SysBike sysBike = sysBikeMapper.selectById(sysRentBike.getBikeId());
        SysUser rentUser = sysUserMapper.selectById(sysRentBike.getRentId());
        SysUser hostUser = sysUserMapper.selectById(sysRentBike.getHostId());
        //返还租户押金
        SysAccount rentUserAccount = sysAccountMapper.selectByRentUserId(rentUser.getId());
        SysAccount hostUserAccount = sysAccountMapper.selectByRentUserId(hostUser.getId());
        SysPayRecord sysPayRecord = new SysPayRecord();
//
        Map<String, Object> resMap = new HashMap<>();
        if (hostUserAccount.getBalance() < deposit) {
            throw new SysException(SysEnum.BALANCE);
        } else {
            hostUserAccount.setBalance(hostUserAccount.getBalance() - deposit);
            rentUserAccount.setBalance(rentUserAccount.getBalance() + deposit);
            int i = sysAccountMapper.updateById(hostUserAccount);
            int i1 = sysAccountMapper.updateById(rentUserAccount);
            //生成返还押金时的记录
            sysPayRecord.setPurpose("押金返还");
            sysPayRecord.setRentId(hostUser.getId());
            sysPayRecord.setHostId(rentUser.getId());
            sysPayRecord.setCreated(LocalDateTime.now());
            sysPayRecord.setBikeId(sysBike.getId());
            sysPayRecord.setPayTime(LocalDateTime.now());
            sysPayRecord.setDeposit(deposit);
            sysPayRecord.setPayAmount(0.0);
            sysPayRecordMapper.insert(sysPayRecord);
//            //修改自行车状态
            sysBike.setRentStatus(1);
            sysBike.setUpdated(LocalDateTime.now());
            sysBike.setRentPhone("");
            sysBike.setRentName("");
            sysBike.setEndDate("");
            sysBike.setIsPay(0);
            sysBike.setIsAgree(0);
            sysBikeMapper.updateById(sysBike);
            //修改租借记录表
            sysRentBike.setReturnDate(LocalDateTime.now());
            sysRentBike.setUpdated(LocalDateTime.now());
            sysRentBikeMapper.updateById(sysRentBike);
            if (i > 0 && i1 > 0) {
                resMap.put("resCode", 2000);
                resMap.put("resMsg", "操作成功！");
                //通知租户还车成功
                webSocket.sendMessage("还车成功！", rentUser.getUsername());
            } else {
                resMap.put("resCode", 3000);
                resMap.put("resMsg", "操作失败！");
            }
        }
        return Result.success(resMap);
    }


}
