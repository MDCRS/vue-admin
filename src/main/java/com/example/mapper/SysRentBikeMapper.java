package com.example.mapper;

import com.example.entity.SysRentBike;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 19846
 * @description 针对表【sys_rent_bike】的数据库操作Mapper
 * @createDate 2022-04-24 22:36:42
 * @Entity com.example.entity.SysRentBike
 */
@Repository
public interface SysRentBikeMapper extends BaseMapper<SysRentBike> {

    List<SysRentBike> selectListByBikeIdAndHostId(@Param("bikeId") Long bikeId, @Param("hostId") Long hostId);

    SysRentBike selectListByBikeIdAndHostIdAndRentId(@Param("bikeId") Long id, @Param("hostId") Long id1, @Param("rentId") Long id2);

    Long getBrandCountByName(String s);
}




