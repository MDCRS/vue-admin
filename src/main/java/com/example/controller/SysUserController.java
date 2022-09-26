package com.example.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.dto.PassDto;
import com.example.common.dto.SysPayRecordDto;
import com.example.common.exception.CaptchaException;
import com.example.common.lang.Constant;
import com.example.common.lang.Result;
import com.example.entity.*;
import com.example.service.SysChargeRecordService;
import com.example.service.SysPayRecordService;
import com.example.utils.StrUtils;
import org.apache.catalina.LifecycleState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author lqz
 * @since 2022-03-25
 */
@RestController
@RequestMapping("/sys/user")
public class SysUserController extends BaseController {

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Resource
    private SysChargeRecordService sysChargeRecordService;

    @Resource
    private SysPayRecordService sysPayRecordService;

    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('sys:user:list')")
    public Result info(@PathVariable("id") Long id) {
        SysUser sysUser = sysUserService.getById(id);
        Assert.notNull(sysUser, "找不到该用户");
        List<SysRole> roles = sysRoleService.listRolesByUserId(id);

        sysUser.setSysRoles(roles);
        return Result.success(sysUser);
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sys:user:list')")
    public Result list(String username) {
        Page<SysUser> page = sysUserService.page(getPage(), new QueryWrapper<SysUser>()
                .like(StrUtil.isNotBlank(username), "username", username));
        page.getRecords().forEach(u -> {
            u.setSysRoles(sysRoleService.listRolesByUserId(u.getId()));
        });
        return Result.success(page);
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('sys:user:save')")
    public Result save(@Validated @RequestBody SysUser sysUser) {
        sysUser.setCreated(LocalDateTime.now());
        passwordEncoder.encode(Constant.DEFAULT_PASSWORD);
        sysUser.setAvatar(Constant.DEFAULT_AVATAR);
        sysUserService.save(sysUser);
        return Result.success(sysUser);
    }

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('sys:user:update')")
    public Result update(@Validated @RequestBody SysUser sysUser) {
        sysUser.setUpdated(LocalDateTime.now());
        sysUserService.updateById(sysUser);
        return Result.success(sysUser);
    }

    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('sys:user:delete')")
    @Transactional
    public Result delete(@RequestBody Long[] ids) {

        sysUserService.removeByIds(Arrays.asList(ids));

        sysUserRoleService.remove(new QueryWrapper<SysUserRole>().in("user_id", ids));

        return Result.success("");
    }

    @PostMapping("/role/{userId}")
    @Transactional
    @PreAuthorize("hasAuthority('sys:user:role')")
    public Result rolePerm(@PathVariable("userId") Long userId, @RequestBody Long[] roleIds) {
        List<SysUserRole> userRoles = new ArrayList<>();
        Arrays.stream(roleIds).forEach(r -> {
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setRoleId(r);
            sysUserRole.setUserId(userId);
            userRoles.add(sysUserRole);
        });
        sysUserRoleService.remove(new QueryWrapper<SysUserRole>().eq("user_id", userId));
        sysUserRoleService.saveBatch(userRoles);
        //删除缓存
        SysUser sysUser = sysUserService.getById(userId);
        sysUserService.clearUserAuthorityInfo(sysUser.getUsername());
        return Result.success("");
    }

    @PostMapping("/repass")
    public Result repass(@RequestBody Long userId) {
        SysUser sysUser = sysUserService.getById(userId);
        sysUser.setPassword(passwordEncoder.encode(Constant.DEFAULT_PASSWORD));
        sysUser.setUpdated(LocalDateTime.now());
        sysUserService.updateById(sysUser);
        return Result.success("");
    }

    @PostMapping("/updatePass")
    @PreAuthorize("hasAuthority('sys:user:repass')")
    public Result updatePass(@Validated @RequestBody PassDto passDto, Principal principal) {
        System.out.println("passDto.toString() = " + passDto.toString());
        SysUser sysUser = sysUserService.getByUsername(principal.getName());
        boolean matches = passwordEncoder.matches(passDto.getCurrentPass(), sysUser.getPassword());
        if (!matches) {
            return Result.success("旧密码不正确");
        }
        sysUser.setPassword(passwordEncoder.encode(passDto.getPassword()));
        sysUser.setUpdated(LocalDateTime.now());
        sysUserService.updateById(sysUser);
        return Result.success("");
    }


    @PostMapping("/updateUserInfo")
    public Result updateUserInfo(HttpServletRequest request, Principal principal) {
        String avatar = request.getParameter("avatar");
        String phone = request.getParameter("phone");
        String username = request.getParameter("username");
        UpdateWrapper<SysUser> updateWrapper = new UpdateWrapper<>();
        SysUser byUsername = sysUserService.getByUsername(principal.getName());
        updateWrapper.eq("id", byUsername.getId());
        SysUser sysUser = new SysUser();
        sysUser.setUsername(username);
        sysUser.setAvatar(avatar);
        sysUser.setPhone(phone);
        String msg = sysUserService.update(sysUser, updateWrapper) ? "修改成功！" : "修改失败！";
        return Result.success(200, msg, null);
    }


    @PostMapping("/charge")
    public Result charge(@RequestBody SysChargeRecord sysChargeRecord) {
        System.out.println("sysChargeRecord = " + sysChargeRecord);
        return sysUserService.toChargeForUser(sysChargeRecord);
    }

    @GetMapping("/chargeRecord/{userId}")
    public Result ioRecord(String orderNum, @PathVariable Long userId) {
        SysUser user = sysUserService.getById(userId);
        Page<SysChargeRecord> page = sysChargeRecordService.page(
                getPage(), new QueryWrapper<SysChargeRecord>()
                        .eq(StringUtils.isNotBlank(orderNum), "order_no", orderNum)
                        .eq("user_id", user.getId())
                        .eq("is_deleted", 0)
                        .orderByDesc("id")
        );
        return Result.success(page);
    }


    @GetMapping("/inRecord/{userId}")
    public Result inRecord(String rentUser, @PathVariable Long userId) {
        SysUser user = sysUserService.getById(userId);
        SysUser byUsername = sysUserService.getByUsername(rentUser);
        Long rent_id = null;
        if (StringUtils.checkValNotNull(byUsername)) {
            rent_id = byUsername.getId();
        }
        Page<SysPayRecord> page = sysPayRecordService.page(
                getPage(), new QueryWrapper<SysPayRecord>()
                        .eq(rent_id != null, "rent_id", rent_id)
                        .eq("host_id", user.getId())
                        .eq("is_deleted", 0)
                        .orderByDesc("id")
        );
        Page<SysPayRecordDto> page1 = convertHostPage(page);
        return Result.success(page1);
    }

    private Page<SysPayRecordDto> convertHostPage(Page<SysPayRecord> page) {
        List<SysPayRecordDto> list = new ArrayList<>();
        page.getRecords().forEach(sysPayRecord -> {
            SysPayRecordDto sysPayRecordDto = new SysPayRecordDto();
            SysUser rentUser = sysUserService.getById(sysPayRecord.getRentId());
            sysPayRecordDto.setPayAmount(sysPayRecord.getPayAmount());
            sysPayRecordDto.setPayTime(sysPayRecord.getPayTime());
            sysPayRecordDto.setUsername(rentUser.getUsername());
            sysPayRecordDto.setDeposit(sysPayRecord.getDeposit());
            sysPayRecordDto.setPurpose(sysPayRecord.getPurpose());
            list.add(sysPayRecordDto);
        });
        Page<SysPayRecordDto> page1 = getPage();
        page1.setRecords(list);
        page1.setPages(page.getPages());
        page1.setSize(page.getSize());
        page1.setTotal(page.getTotal());
        page1.setCurrent(page.getCurrent());
        return page1;
    }


    @GetMapping("/outRecord/{userId}")
    public Result outRecord(String hostUser, @PathVariable Long userId) {
        SysUser user = sysUserService.getById(userId);
        SysUser byUsername = sysUserService.getByUsername(hostUser);
        Long host_id = null;
        if (StringUtils.checkValNotNull(byUsername)) {
            host_id = byUsername.getId();
        }
        Page<SysPayRecord> page = sysPayRecordService.page(
                getPage(), new QueryWrapper<SysPayRecord>()
                        .eq(host_id != null, "host_id", host_id)
                        .eq("rent_id", user.getId())
                        .eq("is_deleted", 0)
                        .orderByDesc("id")
        );
        Page<SysPayRecordDto> page1 = convertRentPage(page);
        return Result.success(page1);
    }


    private Page<SysPayRecordDto> convertRentPage(Page<SysPayRecord> page) {
        List<SysPayRecordDto> list = new ArrayList<>();
        page.getRecords().forEach(sysPayRecord -> {
            SysPayRecordDto sysPayRecordDto = new SysPayRecordDto();
            SysUser hostUser = sysUserService.getById(sysPayRecord.getHostId());
            sysPayRecordDto.setPayAmount(sysPayRecord.getPayAmount());
            sysPayRecordDto.setPayTime(sysPayRecord.getPayTime());
            sysPayRecordDto.setUsername(hostUser.getUsername());
            sysPayRecordDto.setDeposit(sysPayRecord.getDeposit());
            sysPayRecordDto.setPurpose(sysPayRecord.getPurpose());
            list.add(sysPayRecordDto);
        });
        Page<SysPayRecordDto> page1 = getPage();
        page1.setRecords(list);
        page1.setPages(page.getPages());
        page1.setSize(page.getSize());
        page1.setTotal(page.getTotal());
        page1.setCurrent(page.getCurrent());
        return page1;
    }

    private void validate(HttpServletRequest request) {
        String code = request.getParameter("code");
        System.out.println("code = " + code);
        String key = request.getParameter("key");
        System.out.println("key = " + key);
        if (StringUtils.isBlank(code) || StringUtils.isBlank(key)) {
            throw new CaptchaException("验证码不能为空！");
        }
        if (!code.equals(redisUtil.hget(Constant.REG_CODE, key))) {
            throw new CaptchaException("验证码错误！");
        }
        redisUtil.hdel(Constant.REG_CODE, key);
    }

}
