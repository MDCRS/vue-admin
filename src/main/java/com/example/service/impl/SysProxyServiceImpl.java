package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.SysProxy;
import com.example.service.SysProxyService;
import com.example.mapper.SysProxyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 19846
 * @description 针对表【sys_proxy】的数据库操作Service实现
 * @createDate 2022-05-07 21:57:33
 */
@Service
public class SysProxyServiceImpl extends ServiceImpl<SysProxyMapper, SysProxy>
        implements SysProxyService {

    @Autowired
    SysProxyMapper sysProxyMapper;

    @Override
    public List<SysProxy> getProxies() {
        return sysProxyMapper.getProxies();
    }
}




