package com.example.mapper;

import com.example.entity.SysPayRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.SysRentBike;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 19846
 * @description 针对表【sys_pay_record】的数据库操作Mapper
 * @createDate 2022-04-29 22:58:18
 * @Entity com.example.entity.SysPayRecord
 */
@Repository
public interface SysPayRecordMapper extends BaseMapper<SysPayRecord> {

    List<SysPayRecord> selectListByBikeIdAndHostId(@Param("bikeId") Long bikeId, @Param("hostId") Long hostId);
}




