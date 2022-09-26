package com.example.mapper;

import com.example.common.enums.OrderStatus;
import com.example.entity.SysChargeRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author 19846
 * @description 针对表【sys_charge_record】的数据库操作Mapper
 * @createDate 2022-05-02 18:41:42
 * @Entity com.example.entity.SysChargeRecord
 */
@Repository
public interface SysChargeRecordMapper extends BaseMapper<SysChargeRecord> {

    String getChargeRecordStatus(String orderNo);

    void updateStatusByOrderNo(@Param("orderNo") String orderNo, @Param("orderStatus") OrderStatus success);
}




