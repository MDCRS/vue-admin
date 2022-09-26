package com.example.mapper;

import com.example.entity.SysProxy;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 19846
 * @description 针对表【sys_proxy】的数据库操作Mapper
 * @createDate 2022-05-07 21:57:33
 * @Entity com.example.entity.SysProxy
 */
@Repository
public interface SysProxyMapper extends BaseMapper<SysProxy> {

    List<SysProxy> getProxies();

}




