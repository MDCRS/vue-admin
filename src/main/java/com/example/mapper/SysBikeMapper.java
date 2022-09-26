package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.SysBike;
import com.example.entity.SysRentBike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysBikeMapper extends BaseMapper<SysBike> {

    List<SysBike> getBikes(@Param("rent_status") Integer rent_status, @Param("bike_status") Integer bike_status);

    List<SysBike> getBikesOrderByRentCount(@Param("rent_status") Integer rent_status, @Param("bike_status") Integer bike_status);

    SysRentBike selectByBikeIdAndHostIdAndRentId(@Param("bikeId") Long id, @Param("hostId") Long id1, @Param("rentId") Long id2);

    List<SysBike> getAll();

    Long getBrandCountByName(String s);

    Long[] getRentCountByName(String s);

    int getBikeSum();

}
