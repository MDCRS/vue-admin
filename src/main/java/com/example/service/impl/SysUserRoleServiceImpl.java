package com.example.service.impl;

import com.example.entity.SysUserRole;
import com.example.mapper.SysUserRoleMapper;
import com.example.service.SysUserRoleService;
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
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements SysUserRoleService {

    @Override
    public List<SysUserRole> getUserRolesByUserId(Long id) {
        return null;
    }
}
