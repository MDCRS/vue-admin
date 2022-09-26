package com.example.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.dto.RefundForm;
import com.example.common.lang.Constant;
import com.example.common.lang.Result;
import com.example.entity.*;
import com.example.service.SysProfitService;
import com.example.service.SysProxyService;
import com.example.service.SysRemarkService;
import com.example.service.SysUserService;
import com.example.utils.StrUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/sys/bike")
public class SysBikeController extends BaseController {

    @Autowired
    SysRemarkService sysRemarkService;

    @Resource
    SysUserService sysUserService;

    @Autowired
    SysProxyService sysProxyService;

    @Resource
    SysProfitService sysProfitService;

    @GetMapping("/list")
    public Result list(String bikeName, Integer rent_status, Integer bike_status) {
        QueryWrapper<SysBike> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("rent_status", rent_status);
        queryWrapper.eq("bike_status", bike_status);
        queryWrapper.like(StrUtil.isNotBlank(bikeName), "bikeName", bikeName);
        Page<SysBike> page = sysBikeService.page(getPage(), queryWrapper);
        page.getRecords().forEach(bike -> {
            SysUser byId = sysUserService.getById(bike.getUserId());
            bike.setHostName(byId.getUsername());
        });
        return Result.success(page);
    }

    @GetMapping("/homeList")
    public Result homeList(Integer rent_status, Integer bike_status) {
        List<SysBike> sysBikes = sysBikeService.getBikes(rent_status, bike_status);
        if (!StringUtils.checkValNotNull(sysBikes)) {
            return Result.fail("暂时没有符合要求的车辆以供展示！");
        } else {
            return Result.success(sysBikes);
        }
    }

    @GetMapping("/info/{id}")
    public Result info(@PathVariable("id") Long id) {
        SysBike sysBike = sysBikeService.getById(id);
        SysUser sysUser = sysUserService.getById(sysBike.getUserId());
        sysUser.setPassword("");
        Assert.notNull(sysBike, "找不到该车辆");
        Map<String, Object> bikeMap = new HashMap<>();
        bikeMap.put("bikeInfo", sysBike);
        bikeMap.put("userInfo", sysUser);
        return Result.success(bikeMap);
    }

    @GetMapping("/listByUser/{userId}")
    @PreAuthorize("hasAuthority('sys:bike:list')")
    public Result listByUserId(String brand, @PathVariable Long userId) {
        QueryWrapper<SysBike> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.like(StrUtil.isNotBlank(brand), "brand", brand);
        Page<SysBike> page = sysBikeService.page(getPage(), queryWrapper);
        return Result.success(page);
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('sys:bike:save')")
    public Result save(@Validated @RequestBody SysBike sysBike, Principal principal) {
        sysBike.setCreated(LocalDateTime.now());
        if (sysBike.getImage() == null) {
            sysBike.setImage(Constant.DEFAULT_IMAGE);
        }
        sysBike.setIsDeleted(0);
        boolean number = StrUtils.isNumber(principal.getName());
        System.out.println("number = " + number);
        SysUser user = null;
        if (number) {
            user = sysUserService.getUserByPhone(principal.getName());
        } else {
            user = sysUserService.getByUsername(principal.getName());
        }
        sysBike.setUserId(user.getId());
        sysBikeService.save(sysBike);
        return Result.success(sysBike);
    }

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('sys:bike:update')")
    public Result update(@Validated @RequestBody SysBike sysBike) {
        sysBike.setUpdated(LocalDateTime.now());
        sysBikeService.updateById(sysBike);
        return Result.success(sysBike);
    }

    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('sys:bike:delete')")
    @Transactional
    public Result delete(@RequestBody Long[] ids) {
        sysBikeService.removeByIds(Arrays.asList(ids));
        return Result.success("");
    }

    @GetMapping("/orderBikes")
    public Result orderBikes(Integer rent_status, Integer bike_status) {
        List<SysBike> bikes = sysBikeService.getBikesOrderByRentCount(rent_status, bike_status);
        return Result.success(bikes);
    }

    @GetMapping("/remark/{id}")
    public Result getRemark(@PathVariable("id") Long id) {
        System.out.println("id = " + id);
        List<SysRemark> sysRemarks = sysRemarkService.getRemarksByBikeId(id);
        return Result.success(sysRemarks);
    }


    @GetMapping("/proxy")
    public Result getProxy() {
        List<SysProxy> sysProxies = sysProxyService.getProxies();
        return Result.success(sysProxies);
    }

    @PostMapping("/agree/{bikeId}/{type}/{rentName}")
    public Result agree(@PathVariable("bikeId") Integer bikeId,
                        @PathVariable("type") Integer type,
                        @PathVariable("rentName") String rentName) {
        Result result = null;
        try {
            result = sysBikeService.agreeRentBike(bikeId, type, rentName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @GetMapping("/toRefund/{bikeId}/{hostId}/{rentName}")
    public Result toRefund(@PathVariable("bikeId") Long bikeId,
                           @PathVariable("hostId") Long hostId,
                           @PathVariable("rentName") String rentName) {
        System.out.println("bikeId = " + bikeId);
        System.out.println("hostId = " + hostId);
        System.out.println("rentName = " + rentName);
        return sysBikeService.toRefund(bikeId, hostId, rentName);
    }

    @PostMapping("/refund")
    public Result refund(@RequestBody RefundForm refundForm) {
        return sysBikeService.refund(refundForm);
    }

    @GetMapping("/bikeStatistic")
    public Result selectBrandCount() {
        Map<String, Object> resMap = new HashMap<>();
        List<SysBike> list = sysBikeService.getAll();
        System.out.println("list.size() = " + list.size());
        Set<String> bikeSet = new HashSet<>();
        list.forEach(sysBike -> {
            bikeSet.add(sysBike.getBrand());
        });
        int bikeSum = sysBikeService.getBikeSum();
        int userSum = sysUserService.getUserSum();
        double profit = sysProfitService.getProfit();
        Long[] brandCount = new Long[bikeSet.toArray().length];
        Long[] rentCount = new Long[bikeSet.toArray().length];
        for (int i = 0; i < bikeSet.toArray().length; i++) {
            Long[] single = sysBikeService.getRentCountByName((String) bikeSet.toArray()[i]);
            brandCount[i] = sysBikeService.getBrandCountByName((String) bikeSet.toArray()[i]);
            rentCount[i] = 0L;
            for (int j = 0; j < single.length; j++) {
                rentCount[i] += single[j];
            }
        }
        resMap.put("brandName", bikeSet);
        resMap.put("rentCount", rentCount);
        resMap.put("brandCount", brandCount);
        resMap.put("bikeSum", bikeSum);
        resMap.put("userSum", userSum);
        resMap.put("profit", profit);
        return Result.success(resMap);
    }

    @PostMapping("/backDeposit/{bikeRentId}/{deposit}")
    public Result backDeposit(@PathVariable("bikeRentId") Long bikeRentId, @PathVariable("deposit") Double deposit) {
        Result result = sysBikeService.backDeposit(bikeRentId, deposit);
        System.out.println("bikeRentId = " + bikeRentId);
        System.out.println("deposit = " + deposit);
        return result;

    }

}
