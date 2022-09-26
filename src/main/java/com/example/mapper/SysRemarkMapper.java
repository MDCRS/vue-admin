package com.example.mapper;

import com.example.entity.SysRemark;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 19846
 * @description 针对表【sys_remark】的数据库操作Mapper
 * @createDate 2022-05-05 22:47:52
 * @Entity com.example.entity.SysRemark
 */

@Repository
public interface SysRemarkMapper extends BaseMapper<SysRemark> {

    List<SysRemark> getRemarksByBikeId(Long id);
}




