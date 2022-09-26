package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.lang.Result;
import com.example.entity.SysRemark;
import com.example.entity.SysUser;
import com.example.mapper.SysUserMapper;
import com.example.service.SysRemarkService;
import com.example.mapper.SysRemarkMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 19846
 * @description 针对表【sys_remark】的数据库操作Service实现
 * @createDate 2022-05-05 22:47:52
 */
@Service
public class SysRemarkServiceImpl extends ServiceImpl<SysRemarkMapper, SysRemark>
        implements SysRemarkService {

    @Autowired
    SysRemarkMapper sysRemarkMapper;

    @Autowired
    SysUserMapper sysUserMapper;

    @Override
    public List<SysRemark> getRemarksByBikeId(Long id) {
        List<SysRemark> remarks = sysRemarkMapper.getRemarksByBikeId(id);
        remarks.forEach(remark -> {
            SysUser sysUser = sysUserMapper.selectById(remark.getUserId());
            remark.setUsername(sysUser.getUsername());
            remark.setAvatar(sysUser.getAvatar());
        });
        return remarks;
    }
}




