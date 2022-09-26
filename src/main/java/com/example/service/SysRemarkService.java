package com.example.service;

import com.example.entity.SysRemark;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author 19846
 * @description 针对表【sys_remark】的数据库操作Service
 * @createDate 2022-05-05 22:47:52
 */
public interface SysRemarkService extends IService<SysRemark> {

    List<SysRemark> getRemarksByBikeId(Long id);
}
