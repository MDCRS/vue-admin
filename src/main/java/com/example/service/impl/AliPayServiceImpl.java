package com.example.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.example.entity.SysChargeRecord;
import com.example.service.AliPayService;
import com.example.service.SysChargeRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service
@Slf4j
public class AliPayServiceImpl implements AliPayService {


    @Resource
    private SysChargeRecordService sysChargeRecordService;

    @Resource
    private Environment config;

    @Resource
    private AlipayClient alipayClient;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String tradeCreate(SysChargeRecord sysChargeRecord) {

        try {
            log.info("生成充值记录");

            //创建充值记录
            SysChargeRecord sysChargeRecord1 = sysChargeRecordService.createChargeRecord(sysChargeRecord);
            sysChargeRecordService.updateChargeRecordByOrderNo(sysChargeRecord1.getOrderNo());
            //调用支付宝接口
            AlipayTradePagePayRequest alipayTradePagePayRequest = new AlipayTradePagePayRequest();
//
//            request.setNotifyUrl(config.getProperty("alipay.notify-url"));
            alipayTradePagePayRequest.setReturnUrl(config.getProperty("alipay.return-url"));

            //组装当前业务方法的请求参数
            JSONObject bizContent = new JSONObject();
            bizContent.put("out_trade_no", sysChargeRecord1.getOrderNo());
            bizContent.put("total_amount", sysChargeRecord1.getAmount());
            bizContent.put("subject", sysChargeRecord1.getTitle());
            bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");

            alipayTradePagePayRequest.setBizContent(bizContent.toString());

            //执行请求，调用支付宝接口
            AlipayTradePagePayResponse response = alipayClient.pageExecute(alipayTradePagePayRequest);
            if (response.isSuccess()) {
                log.info("调用成功，返回结果 ===> " + response.getBody());
                return response.getBody();
            } else {
                log.info("调用失败，返回码 ===> " + response.getCode() + ", 返回描述 ===> " + response.getMsg());
                throw new RuntimeException("创建支付交易失败");
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            throw new RuntimeException("创建支付交易失败");
        }
    }
}
