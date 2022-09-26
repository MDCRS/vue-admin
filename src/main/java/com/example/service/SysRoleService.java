package com.example.service;

import com.example.entity.SysRole;
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
public interface SysRoleService extends IService<SysRole> {

    List<SysRole> listRolesByUserId(Long id);
}
