package com.example.service;

import com.example.entity.SysChargeRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * @author 19846
 * @description 针对表【sys_charge_record】的数据库操作Service
 * @createDate 2022-05-02 18:41:42
 */
public interface SysChargeRecordService extends IService<SysChargeRecord> {

    SysChargeRecord createChargeRecord(SysChargeRecord sysChargeRecord);

    SysChargeRecord getChargeRecordByOrderNo(String outTradeNo);

    void updateChargeRecord(Map<String, String> params);


    void updateChargeRecordByOrderNo(String orderNo);

}
