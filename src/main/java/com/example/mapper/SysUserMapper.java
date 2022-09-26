package com.example.mapper;

import com.example.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author lqz
 * @since 2022-03-25
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    List<Long> getNavMenuIds(Long userId);

    List<SysUser> listByMenuId(Long menuId);

    int countByUserName(String username);

    SysUser getByUsernameAndPhone(@Param("username") String username, @Param("phone") String phone);

    int getUserSum();

    SysUser getUserByPhone(String phone);
}
