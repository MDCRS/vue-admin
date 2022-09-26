package com.example.task;


import com.example.common.exception.SysEnum;
import com.example.common.exception.SysException;
import com.example.entity.SysBike;
import com.example.entity.SysRentBike;
import com.example.entity.SysUser;
import com.example.service.SysBikeService;
import com.example.service.SysRentBikeService;
import com.example.service.SysUserService;
import com.example.utils.SMSUtil;
import com.example.utils.StrUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;


@Component
@Slf4j
public class NotifyBackBikeTask {

    @Resource
    private SysRentBikeService sysRentBikeService;

    @Resource
    private SysUserService sysUserService;

    @Resource
    private SysBikeService sysBikeService;

    /**
     * 从第0秒开始每隔30秒执行1次，查询创建超过5分钟，并且未支付的订单
     */
    @Scheduled(cron = "0/3600 * * * * ?")
    public void orderConfirm() {
        log.info("orderConfirm 被执行......");
        List<SysRentBike> list = sysRentBikeService.getAllRentBike();
        System.out.println("list = " + list);
        for (SysRentBike sysRentBike : list) {
            SysUser rentUser = sysUserService.getById(sysRentBike.getRentId());
            SysBike bike = sysBikeService.getById(sysRentBike.getBikeId());
            LocalDateTime endDate = sysRentBike.getEndDate();
            LocalDateTime now = LocalDateTime.now();
            Date endD = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());
            Date nowD = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
            System.out.println("endD.getTime() = " + endD.getTime());
            System.out.println("nowD.getTime() = " + nowD.getTime());
            long difference = StrUtils.computationTime(nowD, endD);
            System.out.println("difference = " + difference);
            if (difference >= -1 & difference < 1) {
                //发送短信给自行车主人，通知其自行车已被预租
//                int i = SMSUtil.sendSMS(1409542, rentUser.getPhone(), bike.getBikeName());
//                if (i < 0) {
//                    throw new SysException(SysEnum.SMS);
//                }
                System.out.println("用户名为：" + rentUser.getUsername() + "的用户，租用的自行车" + bike.getBikeName() + "以违约，但没有超过一小时，请尽快归还车辆，否则后果自负！");
            }
        }

    }
}
