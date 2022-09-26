package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.enums.OrderStatus;
import com.example.entity.SysAccount;
import com.example.entity.SysChargeRecord;
import com.example.mapper.SysAccountMapper;
import com.example.service.SysChargeRecordService;
import com.example.mapper.SysChargeRecordMapper;
import com.example.utils.OrderNoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author 19846
 * @description 针对表【sys_charge_record】的数据库操作Service实现
 * @createDate 2022-05-02 18:41:42
 */
@Service
@Slf4j
public class SysChargeRecordServiceImpl extends ServiceImpl<SysChargeRecordMapper, SysChargeRecord>
        implements SysChargeRecordService {

    private final ReentrantLock lock = new ReentrantLock();
    @Autowired
    private SysChargeRecordMapper sysChargeRecordMapper;
    @Autowired
    private SysAccountMapper sysAccountMapper;

    @Override
    public SysChargeRecord createChargeRecord(SysChargeRecord sysChargeRecord) {

        SysChargeRecord chargeRecord = this.getById(sysChargeRecord.getId());
        if (chargeRecord != null) {
            return chargeRecord;
        }

        chargeRecord = new SysChargeRecord();
        chargeRecord.setOrderNo(OrderNoUtils.getOrderNo());
        chargeRecord.setTitle("租金充值");
        chargeRecord.setAmount(sysChargeRecord.getAmount());
        chargeRecord.setUserId(sysChargeRecord.getUserId());
        chargeRecord.setCreated(LocalDateTime.now());
        chargeRecord.setOrderStatus(OrderStatus.NOTPAY.getType());
        chargeRecord.setPaymentType(sysChargeRecord.getPaymentType());

        sysChargeRecordMapper.insert(chargeRecord);

        return chargeRecord;
    }

    @Override
    public SysChargeRecord getChargeRecordByOrderNo(String outTradeNo) {
        QueryWrapper<SysChargeRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no", outTradeNo);
        return sysChargeRecordMapper.selectOne(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateChargeRecord(Map<String, String> params) {

        log.info("处理订单");
        //获取订单号
        String orderNo = params.get("out_trade_no");
        if (lock.tryLock()) {
            try {
                String orderStatus = sysChargeRecordMapper.getChargeRecordStatus(orderNo);
                if (!OrderStatus.NOTPAY.getType().equals(orderStatus)) {
                    return;
                }
                QueryWrapper<SysChargeRecord> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("order_no", orderNo);
                SysChargeRecord sysChargeRecord = sysChargeRecordMapper.selectOne(queryWrapper);
                SysAccount sysAccount = sysAccountMapper.selectByUserId(sysChargeRecord.getUserId());
                sysChargeRecordMapper.updateStatusByOrderNo(orderNo, OrderStatus.SUCCESS);
                sysAccount.setBalance(sysChargeRecord.getAmount());
                sysAccount.setTotalAmount(sysChargeRecord.getAmount());
                sysAccount.setUpdated(LocalDateTime.now());
                sysAccountMapper.updateById(sysAccount);
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    @Transactional
    public void updateChargeRecordByOrderNo(String orderNo) {

        String orderStatus = sysChargeRecordMapper.getChargeRecordStatus(orderNo);
        if (!OrderStatus.NOTPAY.getType().equals(orderStatus)) {
            return;
        }
        QueryWrapper<SysChargeRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no", orderNo);
        SysChargeRecord sysChargeRecord = sysChargeRecordMapper.selectOne(queryWrapper);
        SysAccount sysAccount = sysAccountMapper.selectByUserId(sysChargeRecord.getUserId());
        sysChargeRecordMapper.updateStatusByOrderNo(orderNo, OrderStatus.SUCCESS);
        sysAccount.setBalance(sysAccount.getBalance() + sysChargeRecord.getAmount());
        sysAccount.setTotalAmount(sysAccount.getTotalAmount() + sysChargeRecord.getAmount());
        sysAccount.setUpdated(LocalDateTime.now());
        sysAccountMapper.updateById(sysAccount);
    }
}




