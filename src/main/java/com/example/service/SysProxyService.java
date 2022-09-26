package com.example.service;

import com.example.entity.SysProxy;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author 19846
 * @description 针对表【sys_proxy】的数据库操作Service
 * @createDate 2022-05-07 21:57:33
 */
public interface SysProxyService extends IService<SysProxy> {

    List<SysProxy> getProxies();

}
