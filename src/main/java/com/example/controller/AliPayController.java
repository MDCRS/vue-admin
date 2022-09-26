package com.example.controller;


import com.example.common.lang.Result;
import com.example.entity.SysChargeRecord;
import com.example.service.AliPayService;
import com.example.service.SysChargeRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/api/ali-pay")
@Slf4j
public class AliPayController {

    @Resource
    private AliPayService aliPayService;

    @Resource
    private SysChargeRecordService sysChargeRecordService;


    @PostMapping("/trade/page/pay")
    public Result tradePagePay(@RequestBody SysChargeRecord sysChargeRecord) {
        log.info("统一收单下单并支付页面接口的调用:{}", sysChargeRecord);
        String formStr = aliPayService.tradeCreate(sysChargeRecord);
        return Result.success(formStr);
    }

    @PostMapping("/trade/notify")
    public String tradeNotify(@RequestParam Map<String, String> params) {

        log.info("支付通知正在执行");
        log.info("通知参数 ===> {}", params);

        String result = "failure";

//        try {
//
//            boolean signVerified = AlipaySignature.rsaCertCheckV1(
//                    params,
//                    config.getProperty(public_key),
//                    AlipayConstants.CHARSET_UTF8,
//                    AlipayConstants.SIGN_TYPE_RSA2);
//
//            System.out.println("================="+signVerified+"=====================");
//
//            if (!signVerified) {
//                //验签失败则记录异常日志，并在response中返回failure.
//                log.error("支付成功异步通知验签失败！");
//                return result;
//            }
////
//            // 验签成功后
//            log.info("支付成功异步通知验签成功！");
//
//            //按照支付结果异步通知中的描述，对支付结果中的业务内容进行二次校验，
//            //1 商户需要验证该通知数据中的 out_trade_no 是否为商户系统中创建的订单号
//            String outTradeNo = params.get("out_trade_no");
//            SysChargeRecord chargeRecord = sysChargeRecordService.getChargeRecordByOrderNo(outTradeNo);
//            if (chargeRecord == null) {
//                log.error("充值记录订单不存在");
//                return result;
//            }
//
//            //2 判断 total_amount 是否确实为该订单的实际金额（即商户订单创建时的金额）
//            String totalAmount = params.get("total_amount");
//            double amount = chargeRecord.getAmount();
//            double totalA = Double.parseDouble(totalAmount);
//            if (amount != totalA) {
//                log.error("金额校验失败");
//                return result;
//            }
//
//            //3 校验通知中的 seller_id（或者 seller_email) 是否为 out_trade_no 这笔单据的对应的操作方
//            String sellerId = params.get("seller_id");
//            String sellerIdProperty = config.getProperty("alipay.seller-id");
//            if (!sellerId.equals(sellerIdProperty)) {
//                log.error("商家pid校验失败");
//                return result;
//            }
//
//            //4 验证 app_id 是否为该商户本身
//            String appId = params.get("app_id");
//            String appIdProperty = config.getProperty("alipay.app-id");
//            if (!appId.equals(appIdProperty)) {
//                log.error("appid校验失败");
//                return result;
//            }
//
//            //在支付宝的业务通知中，只有交易通知状态为 TRADE_SUCCESS时，
//            // 支付宝才会认定为买家付款成功。
//            String tradeStatus = params.get("trade_status");
//            if (!"TRADE_SUCCESS".equals(tradeStatus)) {
//                log.error("支付未成功");
//                return result;
//            }
//
//            sysChargeRecordService.updateChargeRecord(params);
//
        result = "success";

//        } catch (AlipayApiException e) {
//            e.printStackTrace();
//        }

        return result;

    }
}
