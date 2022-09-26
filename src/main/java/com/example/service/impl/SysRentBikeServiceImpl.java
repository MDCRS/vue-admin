package com.example.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.dto.BackBike;
import com.example.common.dto.PayBikeInfo;
import com.example.common.exception.SysEnum;
import com.example.common.exception.SysException;
import com.example.common.lang.Result;
import com.example.entity.*;
import com.example.mapper.*;
import com.example.service.SysRentBikeService;
import com.example.utils.SMSUtil;
import com.example.utils.StrUtils;
import org.apache.ibatis.jdbc.Null;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author 19846
 * @description 针对表【sys_rent_bike】的数据库操作Service实现
 * @createDate 2022-04-24 22:36:42
 */
@Service
public class SysRentBikeServiceImpl extends ServiceImpl<SysRentBikeMapper, SysRentBike>
        implements SysRentBikeService {

    @Autowired
    SysRentBikeMapper sysRentBikeMapper;

    @Autowired
    SysBikeMapper sysBikeMapper;

    @Autowired
    SysUserMapper sysUserMapper;

    @Autowired
    SysAccountMapper sysAccountMapper;

    @Autowired
    SysPayRecordMapper sysPayRecordMapper;

    @Autowired
    SysRemarkMapper sysRemarkMapper;
    @Resource
    BackBike backBike;
    @Resource
    private WebSocket webSocket;
    @Resource
    private ProfitRecordMapper profitRecordMapper;

    @Resource
    private SysProfitMapper sysProfitMapper;

    @Override
    @Transactional
    public Result toSaveRentBike(SysRentBike sysRentBike) {
        System.out.println("sysRentBike = " + sysRentBike);
        //获取租单中的租户信息，车主信息，自行车信息
        //添加到个人租单
        SysUser rentUser = sysUserMapper.selectById(sysRentBike.getRentId());  //租户信息
        SysBike sysBike = sysBikeMapper.selectById(sysRentBike.getBikeId());   //自行车信息
        SysUser hostUser = sysUserMapper.selectById(sysBike.getUserId()); //车主信息
        //完善租单信息
        sysRentBike.setHostId(hostUser.getId());
        sysRentBike.setCreated(LocalDateTime.now());
        //添加个人租车记录
        String res = sysRentBikeMapper.insert(sysRentBike) > 0 ? "提交租单成功，等待车主同意！" : "提交租单失败！";
        //修改车辆信息
        sysBike.setRentStatus(2);
        sysBike.setRentName(rentUser.getUsername());
        sysBike.setRentPhone(rentUser.getPhone());
        sysBike.setEndDate(sysRentBike.getEndDate().toString());
        sysBike.setUpdated(LocalDateTime.now());
        sysBikeMapper.updateById(sysBike);
        //发送短信给自行车主人，通知其自行车已被预租
        int i = SMSUtil.sendSMS(1406082, hostUser.getPhone(), rentUser);
        if (i < 0) {
            throw new SysException(SysEnum.SMS);
        }
        System.out.println("发送短信给自行车主人，通知其自行车已被预租");
        return Result.success(res);
    }

    @Override
    public Result calTime(Long rentBikeId, Double deposit, String endDate) {
        String[] ts = endDate.split("T");
        String endDate1 = ts[0] + " " + ts[1];
        Date eDate = StrUtils.StrToDate(endDate1);
        Date rDate = new Date();
        long eTime = eDate.getTime();
        System.out.println("eTime = " + eTime);
        long rTime = rDate.getTime();
        System.out.println("rTime = " + rTime);
        long oneHour = 60 * 60 * 1000;
        long twoHour = oneHour * 2;
        long threeHour = oneHour * 3;
        long fourHour = oneHour * 4;
        long duringTime = rTime - eTime;
        System.out.println("duringTime = " + duringTime);
        SysRentBike sysRentBike = sysRentBikeMapper.selectById(rentBikeId);
        SysBike sysBike = sysBikeMapper.selectById(sysRentBike.getBikeId());
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("rentBikeId", rentBikeId);
        if (duringTime > 0) {
            //没有按时还车
            if (duringTime <= oneHour) {
                //违约小于等于一小时，扣除信用值20点
                System.out.println("违约小于等于一小时");
                resMap.put("creditCount", 20);
                resMap.put("deposit", sysBike.getDeposit());
                resMap.put("defaultMsg", "您违约小于一小时");
                return Result.success(resMap);
            } else if (duringTime <= twoHour) {
                //违约小于等于两小时
                System.out.println("违约小于等于两小时");
                resMap.put("creditCount", 40);
                resMap.put("deposit", sysBike.getDeposit() / 2);
                resMap.put("defaultMsg", "您违约超过一小时，但小于两小时！");
                return Result.success(resMap);
            } else if (duringTime <= threeHour) {
                //违约小于等于三小时
                System.out.println("违约小于等于三小时");
                resMap.put("creditCount", 80);
                resMap.put("deposit", sysBike.getDeposit() / 4);
                resMap.put("defaultMsg", "您违约超过两小时小时，但小于三小时！");
                return Result.success(resMap);
            } else if (duringTime <= fourHour) {
                //违约小于等于四小时
                System.out.println("违约小于等于四小时");
                resMap.put("creditCount", 160);
                resMap.put("deposit", 0.0);
                resMap.put("defaultMsg", "您违约超过三小时，但小于四小时！");
                return Result.success(resMap);
            } else {
                System.out.println("请联系租户，必要时请报警！");
                resMap.put("creditCount", 200);
                resMap.put("deposit", 0.0);
                resMap.put("defaultMsg", "您违约超过四小时！");
                return Result.success(resMap);
            }
        } else {
            //按时还车
            System.out.println("按时还车");
            resMap.put("creditCount", 0);
            resMap.put("deposit", sysBike.getDeposit());
            resMap.put("defaultMsg", "您没有违约！");
            return Result.success(resMap);
        }
    }

    @Override
    @Transactional
    public Result backBike(BackBike bb) {
        //修改自行车租借状态为 还车中，通过短信发送通知给车主对方还车，线下还车完成，车主退还租金，修改租车状态为已归还
        SysRentBike sysRentBike = sysRentBikeMapper.selectById(bb.getBikeRentId());
        SysBike sysBike = sysBikeMapper.selectById(sysRentBike.getBikeId());
        SysUser hostUser = sysUserMapper.selectById(sysBike.getUserId());
        SysUser rentUser = sysUserMapper.selectById(sysRentBike.getRentId());
        sysBike.setRentStatus(3);
        sysBikeMapper.updateById(sysBike);
        //添加评价
        SysRemark sysRemark = new SysRemark();
        sysRemark.setBikeId(sysBike.getId());
        sysRemark.setUserId(sysRentBike.getRentId());
        if (bb.getRemark() == null || Objects.equals(bb.getRemark(), "")) {
            sysRemark.setContent("默认好评!");
        } else {
            sysRemark.setContent(bb.getRemark());
        }
        sysRemark.setCreated(LocalDateTime.now());
        sysRemarkMapper.insert(sysRemark);
//        修改租户信用值
        rentUser.setCreditValue(rentUser.getCreditValue() - bb.getCreditCount());
        sysUserMapper.updateById(rentUser);
        //发给车主的短信,通知租户的还车
        int i5 = SMSUtil.sendSMS(1395395, hostUser.getPhone(), rentUser);
        if (i5 < 0) {
            throw new SysException(SysEnum.SMS);
        }
        webSocket.sendMessage("租户准备归还车名为：" + sysBike.getBikeName() + "的自行车", hostUser.getUsername());
        backBike.setBikeRentId(bb.getBikeRentId());
        backBike.setDeposit(bb.getDeposit());
        System.out.println("backBike = " + bb);
        return Result.success("操作成功，等待车主确认！");
    }

    @Override
    @Transactional
    public Result payRent(PayBikeInfo payBikeInfo) {
        //获取相关信息
        SysRentBike sysRentBike = sysRentBikeMapper.selectById(payBikeInfo.getRentBikeId());
        SysUser rentUser = sysUserMapper.selectById(sysRentBike.getRentId());
        SysUser hostUser = sysUserMapper.selectById(sysRentBike.getHostId());
        SysBike sysBike = sysBikeMapper.selectById(sysRentBike.getBikeId());
        SysAccount rentUserAccount = sysAccountMapper.selectByUserId(rentUser.getId());
        SysAccount hostUserAccount = sysAccountMapper.selectByUserId(hostUser.getId());
        Double sum = payBikeInfo.getRentNum() + payBikeInfo.getDeposit();
        //判断租户账户余额是否足够支付
        if (rentUserAccount.getBalance() < sum) {
            return Result.success("您的账户余额不足，请先充值");
        }
        //平台收益记录
        ProfitRecord profitRecord = new ProfitRecord();
        profitRecord.setCoin(sum * 0.03);
        profitRecord.setCreated(LocalDateTime.now());
        profitRecord.setUpdated(LocalDateTime.now());
        profitRecordMapper.insert(profitRecord);
        SysProfit sysProfit = sysProfitMapper.selectOne(null);
        sysProfit.setAccount(sysProfit.getAccount() + (sum * 0.03));
        sysProfit.setUpdated(LocalDateTime.now());
        sysProfitMapper.updateById(sysProfit);
        //足够去支付
        rentUserAccount.setBalance(rentUserAccount.getBalance() - sum);
        hostUserAccount.setBalance(hostUserAccount.getBalance() + (sum - (sum * 0.03)));
        sysAccountMapper.updateById(rentUserAccount);
        sysAccountMapper.updateById(hostUserAccount);
        webSocket.sendMessage("租户" + rentUser.getUsername() + ",已支付车辆名为：" + sysBike.getBikeName() + "的租金，支付总金额为：" + sum + "元，注意对方取车!", hostUser.getUsername());
        //添加支付记录
        SysPayRecord sysPayRecord = new SysPayRecord();
        sysPayRecord.setDeposit(payBikeInfo.getDeposit());
        sysPayRecord.setPayTime(LocalDateTime.now());
        sysPayRecord.setRentId(sysRentBike.getRentId());
        sysPayRecord.setPayAmount(payBikeInfo.getRentNum());
        sysPayRecord.setBikeId(sysBike.getId());
        sysPayRecord.setCreated(LocalDateTime.now());
        sysPayRecord.setHostId(hostUser.getId());
        sysPayRecord.setPurpose("租车");
        sysPayRecordMapper.insert(sysPayRecord);
        //修改自行车相关信息
        sysBike.setRentStatus(0);
        sysBike.setUpdated(LocalDateTime.now());
        sysBike.setRentCount(sysBike.getRentCount() + 1);
        sysBike.setIsPay(1);
        sysBikeMapper.updateById(sysBike);
        //租单信息修改
        sysRentBike.setIsPay(1);
        sysRentBikeMapper.updateById(sysRentBike);
        return Result.success("租车成功，快去去取车吧！");
    }

    @Override
    public List<SysRentBike> getAllRentBike() {
        return sysRentBikeMapper.selectList(
                new QueryWrapper<SysRentBike>()
                        .eq("is_deleted", 0)
                        .isNull("return_date"));
    }

    @Override
    public Long getBrandCountByName(String s) {
        return sysRentBikeMapper.getBrandCountByName(s);
    }


}




