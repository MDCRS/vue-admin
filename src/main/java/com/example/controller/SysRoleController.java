package com.example.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.lang.Constant;
import com.example.common.lang.Result;
import com.example.entity.SysRole;
import com.example.entity.SysRoleMenu;
import com.example.entity.SysUserRole;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author lqz
 * @since 2022-03-25
 */
@RestController
@RequestMapping("/sys/role")
public class SysRoleController extends BaseController {

    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('sys:role:list')")
    public Result info(@PathVariable(name = "id") Long id) {
        SysRole sysRole = sysRoleService.getById(id);
        List<SysRoleMenu> sysRoleMenus = sysRoleMenuService.list(new QueryWrapper<SysRoleMenu>().eq("role_id", id));
        List<Long> menuIds = sysRoleMenus.stream().map(p -> p.getMenuId()).collect(Collectors.toList());
        sysRole.setMenuIds(menuIds);
        System.out.println("SysRoleController->sysRole = " + sysRole);
        return Result.success(sysRole);
    }


    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sys:role:list')")
    public Result list(String name) {
        Page<SysRole> pageData = sysRoleService.page(getPage(),
                new QueryWrapper<SysRole>()
                        .like(StrUtil.isNotBlank(name), "name", name)
        );
        System.out.println("SysRoleController->list = " + pageData);
        return Result.success(pageData);
    }


    @PostMapping("/save")
    @PreAuthorize("hasAuthority('sys:role:save')")
    public Result info(@Validated @RequestBody SysRole sysRole) {
        sysRole.setCreated(LocalDateTime.now());
        sysRole.setStatus(Constant.STATUS_ON);
        sysRoleService.save(sysRole);
        return Result.success("");
    }


    @PostMapping("/update")
    @PreAuthorize("hasAuthority('sys:role:update')")
    public Result update(@Validated @RequestBody SysRole sysRole) {
        sysRole.setUpdated(LocalDateTime.now());
        sysRoleService.updateById(sysRole);
        // 更新缓存
        sysUserService.clearUserAuthorityInfoByRoleId(sysRole.getId());
        return Result.success(sysRole);
    }


    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('sys:role:delete')")
    @Transactional
    public Result delete(@RequestBody Long[] ids) {

        sysRoleService.removeByIds(Arrays.asList(ids));

        // 删除中间表
        sysUserRoleService.remove(new QueryWrapper<SysUserRole>().in("role_id", ids));
        sysRoleMenuService.remove(new QueryWrapper<SysRoleMenu>().in("role_id", ids));

        // 缓存同步删除
        Arrays.stream(ids).forEach(id -> {
            // 更新缓存
            sysUserService.clearUserAuthorityInfoByRoleId(id);
        });
        return Result.success("");
    }


    @PostMapping("/perm/{roleId}")
    @PreAuthorize("hasAuthority('sys:role:perm')")
    @Transactional
    public Result perm(@PathVariable(name = "roleId") Long roleId, @RequestBody Long[] menuIds) {
        List<SysRoleMenu> sysRoleMenus = new ArrayList<>();

        Arrays.stream(menuIds).forEach(menuId -> {
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setMenuId(menuId);
            roleMenu.setRoleId(roleId);

            sysRoleMenus.add(roleMenu);
        });

        // 先删除原来的记录，再保存新的
        sysRoleMenuService.remove(new QueryWrapper<SysRoleMenu>().eq("role_id", roleId));
        sysRoleMenuService.saveBatch(sysRoleMenus);

        // 删除缓存
        sysUserService.clearUserAuthorityInfoByRoleId(roleId);
        return Result.success(menuIds);
    }
}
