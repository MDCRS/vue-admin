package com.example.service;

import com.example.entity.SysUserRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lqz
 * @since 2022-03-25
 */
public interface SysUserRoleService extends IService<SysUserRole> {

    List<SysUserRole> getUserRolesByUserId(Long id);
}
