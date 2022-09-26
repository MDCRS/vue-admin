package com.example.service;

import com.example.common.lang.Result;
import com.example.entity.SysChargeRecord;
import com.example.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lqz
 * @since 2022-03-25
 */
public interface SysUserService extends IService<SysUser> {

    SysUser getByUsername(String username);

    String getUserAuthorityInfo(Long userId);

    void clearUserAuthorityInfo(String username);

    void clearUserAuthorityInfoByRoleId(Long roleId);

    void clearUserAuthorityInfoByMenuId(Long menuId);

    int countByUserName(String username);

    SysUser getByUsernameAndPhone(String username, String phone);

    Result toChargeForUser(SysChargeRecord sysChargeRecord);


    int getUserSum();

    SysUser getUserByPhone(String phone);
}
