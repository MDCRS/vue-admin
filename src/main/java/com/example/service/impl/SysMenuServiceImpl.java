package com.example.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.common.dto.SysMenuDto;
import com.example.entity.SysMenu;
import com.example.entity.SysUser;
import com.example.mapper.SysMenuMapper;
import com.example.mapper.SysUserMapper;
import com.example.service.SysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.service.SysUserService;
import com.example.utils.StrUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lqz
 * @since 2022-03-25
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Autowired
    SysUserService sysUserService;

    @Autowired
    SysUserMapper sysUserMapper;

    @Override
    public List<SysMenuDto> getCurrentUserNav() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean number = StrUtils.isNumber(username);
        SysUser user = null;
        if (number) {
            user = sysUserService.getUserByPhone(username);
        } else {
            user = sysUserService.getByUsername(username);
        }
        List<Long> navMenuIds = sysUserMapper.getNavMenuIds(user.getId());
        List<SysMenu> menus = this.listByIds(navMenuIds);

        //转树状结构
        List<SysMenu> menuTree = buildTreeMenu(menus);
        //实体dto
        return convert(menuTree);
    }

    @Override
    public List<SysMenu> tree() {

        List<SysMenu> sysMenus = this.list(new QueryWrapper<SysMenu>().orderByAsc("orderNum"));
        return buildTreeMenu(sysMenus);
    }

    private List<SysMenuDto> convert(List<SysMenu> menuTree) {
        List<SysMenuDto> menuDtos = new ArrayList<>();
        menuTree.forEach(m -> {
            SysMenuDto dto = new SysMenuDto();
            dto.setId(m.getId());
            dto.setName(m.getPerms());
            dto.setIcon(m.getIcon());
            dto.setTitle(m.getName());
            dto.setComponent(m.getComponent());
            dto.setPath(m.getPath());
            if (m.getChildren().size() > 0) {

                //子节点调用当前方法进行转化
                dto.setChildren(convert(m.getChildren()));
            }
            menuDtos.add(dto);
        });
        return menuDtos;
    }

    private List<SysMenu> buildTreeMenu(List<SysMenu> menus) {
        List<SysMenu> finalMenu = new ArrayList<>();
        //先找到各自的孩子
        for (SysMenu menu : menus) {
            for (SysMenu menu1 : menus) {
                if (menu.getId() == menu1.getParentId()) {
                    menu.getChildren().add(menu1);
                }
            }
            if (menu.getParentId() == 0L) {
                finalMenu.add(menu);
            }
        }
        //提取父节点
//        System.out.println("SysMenuServiceImpl->JSONUtil.toJsonStr(finalMenu) = " + JSONUtil.toJsonStr(finalMenu));
        return finalMenu;
    }
}
