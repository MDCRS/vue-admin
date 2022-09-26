package com.example.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.dto.BackBike;
import com.example.common.dto.PayBikeInfo;
import com.example.common.dto.RentBikeDto;
import com.example.common.lang.Result;
import com.example.entity.*;
import com.example.utils.StrUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.security.Principal;
import java.text.DecimalFormat;
import java.util.*;

@RestController
@RequestMapping("/sys/bikeRent")
public class SysRentBikeController extends BaseController {

    @Resource
    BackBike backBike;

    @GetMapping("/list")
    public Result list(Principal principal) {
        QueryWrapper<SysRentBike> queryWrapper = new QueryWrapper<>();
        boolean number = StrUtils.isNumber(principal.getName());
        System.out.println("number = " + number);
        SysUser user = null;
        if (number) {
            user = sysUserService.getUserByPhone(principal.getName());
        } else {
            user = sysUserService.getByUsername(principal.getName());
        }
        queryWrapper.eq("rent_id", user.getId());
        queryWrapper.orderByDesc("id");
        Page<SysRentBike> page = sysRentBikeService.page(getPage(), queryWrapper);
        Page<RentBikeDto> page1 = convertPage(page);
        return Result.success(page1);
    }

    @PostMapping("/save")
    public Result saveRentBikeInfo(@Validated @RequestBody SysRentBike sysRentBike) {
        return sysRentBikeService.toSaveRentBike(sysRentBike);
    }

    @PostMapping("/backBike")
    public Result backBike(@RequestBody BackBike backBike) {
        Result result = sysRentBikeService.backBike(backBike);
        return Result.success(result);
    }

    @PostMapping("/delete")
//    @PreAuthorize("hasAuthority('sys:bike:delete')")
    @Transactional
    public Result delete(@RequestBody Long[] ids) {
        sysRentBikeService.removeByIds(Arrays.asList(ids));
        return Result.success("删除成功！");
    }

    /**
     * @param startDateTime
     * @param endDateTime
     * @param hourPrice
     * @param dayPrice
     * @param unit
     * @return
     */
    @GetMapping("/toCountPrice")
    public Result toCountPrice(String startDateTime, String endDateTime, Double hourPrice, Double dayPrice, Integer unit) {
        if (startDateTime == null || endDateTime == null) {
            return Result.success(0.0);
        }
        Date date1 = StrUtils.StrToDate(startDateTime);
        Date date2 = StrUtils.StrToDate(endDateTime);
        Long s = null;
        Double total = null;
        DecimalFormat df = null;
        String format = null;
        Map<String, Object> resMap = new HashMap<>();
        if (unit == 0) {
            s = StrUtils.computationTime(date1, date2);
            total = s * hourPrice;
            df = new DecimalFormat("#.00");
            format = df.format(total);
            resMap.put("key", 2001);
            resMap.put("format", format);
        } else if (unit == 1) {
            if (StrUtils.computationDay(date1, date2) < 1) {
                resMap.put("key", 3001);
                resMap.put("strMsg", "您的租用时间不足一天,不能勾选按天租用!");
                return Result.success(resMap);
            }
            s = StrUtils.computationDay(date1, date2);
            total = s * dayPrice;
            df = new DecimalFormat("#.00");
            format = df.format(total);
            resMap.put("key", 2001);
            resMap.put("format", format);
        }
        System.out.println("startDateTime = " + startDateTime);
        System.out.println("endDateTime = " + endDateTime);
        System.out.println("date1 = " + date1.getTime());
        System.out.println("date2 = " + date2.getTime());
        return Result.success(resMap);
    }


    @GetMapping("/calTime")
    public Result calTime(Long rentBikeId, Double deposit, String endDate) {
        Result result = sysRentBikeService.calTime(rentBikeId, deposit, endDate);
        return Result.success(result);

    }


    @PostMapping("/payRent")
    public Result payRent(@RequestBody PayBikeInfo payBikeInfo) {
        return sysRentBikeService.payRent(payBikeInfo);
    }

    @GetMapping("/returnBikeMsg")
    public Result returnBikeMsg() {
        return Result.success(backBike);
    }


    private RentBikeDto convertDto(SysRentBike sysRentBike) {
        RentBikeDto rentBikeDto = new RentBikeDto();
        SysBike bike = sysBikeService.getById(sysRentBike.getBikeId());
        SysUser host = sysUserService.getById(sysRentBike.getHostId());
        rentBikeDto.setId(sysRentBike.getId());
        rentBikeDto.setBrand(bike.getBrand());
        rentBikeDto.setImage(bike.getImage());
        rentBikeDto.setBikeName(bike.getBikeName());
        rentBikeDto.setHostName(host.getUsername());
        rentBikeDto.setStartDate(sysRentBike.getStartDate());
        rentBikeDto.setEndDate(sysRentBike.getEndDate());
        rentBikeDto.setReturnDate(sysRentBike.getReturnDate());
        rentBikeDto.setRentPrice(sysRentBike.getRentPrice());
        rentBikeDto.setHostPhone(host.getPhone());
        rentBikeDto.setDeposit(bike.getDeposit());
        rentBikeDto.setIsPay(sysRentBike.getIsPay());
        rentBikeDto.setIsAgree(sysRentBike.getIsAgree());
        return rentBikeDto;
    }

    private Page<RentBikeDto> convertPage(Page<SysRentBike> page) {
        List<RentBikeDto> list = new ArrayList<>();
        page.getRecords().forEach(sysRentBike -> {
            RentBikeDto rentBikeDto = convertDto(sysRentBike);
            list.add(rentBikeDto);
        });
        Page<RentBikeDto> page1 = getPage();
        page1.setRecords(list);
        page1.setCurrent(page.getCurrent());
        page1.setPages(page.getPages());
        page1.setTotal(page.getTotal());
        page1.setSize(page.getSize());
        return page1;
    }
}
