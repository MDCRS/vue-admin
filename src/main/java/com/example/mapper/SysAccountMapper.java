package com.example.mapper;

import com.example.entity.SysAccount;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author 19846
 * @description 针对表【sys_account】的数据库操作Mapper
 * @createDate 2022-04-29 22:57:09
 * @Entity com.example.entity.SysAccount
 */
@Repository
public interface SysAccountMapper extends BaseMapper<SysAccount> {

    SysAccount selectByRentUserId(Long rentId);

    SysAccount selectByUserId(Long userId);
}




