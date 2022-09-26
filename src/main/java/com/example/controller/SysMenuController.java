package com.example.controller;


import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.common.dto.SysMenuDto;
import com.example.entity.SysMenu;
import com.example.entity.SysRoleMenu;
import com.example.utils.StrUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import com.example.common.lang.Result;
import com.example.entity.SysUser;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
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
@RequestMapping("/sys/menu")
public class SysMenuController extends BaseController {


    @GetMapping("/nav")
    public Result nav(Principal principal) {
        boolean number = StrUtils.isNumber(principal.getName());
        SysUser user = null;
        if (number) {
            user = sysUserService.getUserByPhone(principal.getName());
        } else {
            user = sysUserService.getByUsername(principal.getName());
        }
        //获取权限信息
        String authorityInfo = sysUserService.getUserAuthorityInfo(user.getId());
        String[] authorityInfoArray = StringUtils.tokenizeToStringArray(authorityInfo, ",");
        //获取导航栏信息
        List<SysMenuDto> navs = sysMenuService.getCurrentUserNav();
        return Result.success(
                MapUtil.builder()
                        .put("authoritys", authorityInfoArray)
                        .put("nav", navs).map());
    }


    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('sys:menu:list')")
    public Result info(@PathVariable(name = "id") Long id) {
        return Result.success(sysMenuService.getById(id));
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sys:menu:list')")
    public Result list() {
        List<SysMenu> menus = sysMenuService.tree();
        return Result.success(menus);
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('sys:menu:save')")
    public Result save(@Validated @RequestBody SysMenu sysMenu) {
        sysMenu.setCreated(LocalDateTime.now());
        sysMenuService.save(sysMenu);
        return Result.success(sysMenu);
    }

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('sys:menu:update')")
    public Result update(@Validated @RequestBody SysMenu sysMenu) {
        sysMenu.setUpdated(LocalDateTime.now());
        sysMenuService.updateById(sysMenu);
        sysUserService.clearUserAuthorityInfoByMenuId(sysMenu.getId());
        return Result.success(sysMenu);
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('sys:menu:delete')")
    public Result delete(@PathVariable(name = "id") Long id) {
        int count = sysMenuService.count(new QueryWrapper<SysMenu>().eq("parent_id", id));
        if (count > 0) {
            return Result.fail("请先删除子菜单");
        }
        sysUserService.clearUserAuthorityInfoByMenuId(id);
        sysMenuService.removeById(id);
        sysRoleMenuService.remove(new QueryWrapper<SysRoleMenu>().eq("menu_id", id));
        return Result.success("操作成功！");
    }

}
