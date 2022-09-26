package com.example.utils;

import com.example.common.lang.Constant;
import com.example.common.lang.Result;
import com.example.entity.SysUser;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class SMSUtil {

    //注册发送验证码
    public static Result sendSMS(String tel, RedisUtil redisUtil) {
        Map<String, Object> map = new HashMap<>();
        String reStr = ""; //定义返回值
        // 短信应用SDK AppID  1400开头
        int appId = 1400670405;
        // 短信应用SDK AppKey
        String appKey = "34e6460754bd221f7f93233a00b21040";
        // 短信模板ID，需要在短信应用中申请
        int templateId = 1382171;
        // 签名，使用的是签名内容，而不是签名ID
        String smsSign = "阿斯特拉猎者公众号";
        //随机生成四位验证码的工具类
        String code = CodeUtil.codeUtils();
        String key = null;
        try {
            //参数，一定要对应短信模板中的参数顺序和个数，
            String[] params = {code};
            //创建ssender对象
            SmsSingleSender sSender = new SmsSingleSender(appId, appKey);
            //发送
            SmsSingleSenderResult result = sSender.sendWithParam("86", tel, templateId, params, smsSign, "", "");
            if (result.result != 0) {
                reStr = "error";
            }
            //把生成的验证码存到redis中
            key = UUID.randomUUID().toString();
            redisUtil.hset(Constant.REG_CODE, key, code, 60);
            reStr = "success";
        } catch (Exception e) {
            e.printStackTrace();
        }
        map.put("reStr", reStr);
        map.put("key", key);
        return Result.success(map);
    }

    //租车或还车成功后发送通知
    public static int sendSMS(int templateId, String phone, SysUser sysUser) {
        int reStr = 0; //定义返回值
        // 短信应用SDK AppID  1400开头
        int appId = 1400670405;
        // 短信应用SDK AppKey
        String appKey = "34e6460754bd221f7f93233a00b21040";
        // 短信模板ID，需要在短信应用中申请
        // 签名，使用的是签名内容，而不是签名ID
        String smsSign = "阿斯特拉猎者公众号";
        try {
            //参数，一定要对应短信模板中的参数顺序和个数，
            String[] params = {sysUser.getUsername(), sysUser.getPhone()};
            //创建ssender对象
            SmsSingleSender sSender = new SmsSingleSender(appId, appKey);
            //发送
            SmsSingleSenderResult result = sSender.sendWithParam("86", phone, templateId, params, smsSign, "", "");
            if (result.result != 0) {
                reStr = 3000;
            }
            reStr = 2000;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reStr;
    }

    //租车或还车成功后发送通知
    public static int sendSMS(int templateId, String phone, String bikeName) {
        int reStr = 0; //定义返回值
        // 短信应用SDK AppID  1400开头
        int appId = 1400670405;
        // 短信应用SDK AppKey
        String appKey = "34e6460754bd221f7f93233a00b21040";
        // 短信模板ID，需要在短信应用中申请
        // 签名，使用的是签名内容，而不是签名ID
        String smsSign = "阿斯特拉猎者公众号";
        try {
            //参数，一定要对应短信模板中的参数顺序和个数，
            String[] params = {bikeName};
            //创建ssender对象
            SmsSingleSender sSender = new SmsSingleSender(appId, appKey);
            //发送
            SmsSingleSenderResult result = sSender.sendWithParam("86", phone, templateId, params, smsSign, "", "");
            if (result.result != 0) {
                reStr = 3000;
            }
            reStr = 2000;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reStr;
    }

    //登录发送验证码
    public Result sendSMSLogin(String phone, RedisUtil redisUtil) {
        Map<String, Object> map = new HashMap<>();
        String reStr = ""; //定义返回值
        // 短信应用SDK AppID  1400开头
        int appId = 1400670405;
        // 短信应用SDK AppKey
        String appKey = "34e6460754bd221f7f93233a00b21040";
        // 短信模板ID，需要在短信应用中申请
        int templateId = 1432269;
        // 签名，使用的是签名内容，而不是签名ID
        String smsSign = "阿斯特拉猎者公众号";
        //随机生成四位验证码的工具类
        String code = CodeUtil.codeUtils();
        try {
            //参数，一定要对应短信模板中的参数顺序和个数，
            String[] params = {code};
            //创建ssender对象
            SmsSingleSender sSender = new SmsSingleSender(appId, appKey);
            //发送
            SmsSingleSenderResult result = sSender.sendWithParam("86", phone, templateId, params, smsSign, "", "");
            if (result.result != 0) {
                reStr = "error";
            }
            //把生成的验证码存到redis中
            redisUtil.set(phone, code, 60);
            reStr = "success";
        } catch (Exception e) {
            e.printStackTrace();
        }
        map.put("reStr", reStr);
        return Result.success(map);
    }

}


