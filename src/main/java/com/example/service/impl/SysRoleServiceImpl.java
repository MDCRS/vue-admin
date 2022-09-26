package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.entity.SysRole;
import com.example.mapper.SysRoleMapper;
import com.example.service.SysRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Override
    public List<SysRole> listRolesByUserId(Long userId) {
        List<SysRole> sysRoles = this.list(new QueryWrapper<SysRole>().inSql("id", "select role_id from sys_user_role where user_id = " + userId));
        return sysRoles;
    }
}
