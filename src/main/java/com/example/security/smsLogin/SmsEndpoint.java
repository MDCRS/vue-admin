package com.example.security.smsLogin;

import cn.hutool.core.util.RandomUtil;
import com.example.common.lang.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/sms")
@Slf4j
public class SmsEndpoint {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 发送验证码接口
     *
     * @param phone
     * @return
     */
    @GetMapping("/send/code/{phone}")
    public Result msmCode(@PathVariable("phone") String phone) {
        // 1. 获取到手机号
        log.info(phone + "请求获取验证码");
        // 2. 模拟调用短信平台获取验证码，以手机号为KEY，验证码为值，存入Redis，过期时间一分钟
        String code = RandomUtil.randomNumbers(6);
        redisTemplate.opsForValue().setIfAbsent(phone, code, 120, TimeUnit.SECONDS);
        String saveCode = redisTemplate.opsForValue().get(phone);// 缓存中的code
        Long expire = redisTemplate.opsForValue().getOperations().getExpire(phone, TimeUnit.SECONDS); // 查询过期时间
        // 3. 验证码应该通过短信发给用户，这里直接返回吧
        Map<String, String> result = new HashMap<>();
        result.put("code", saveCode);
        result.put("expire", expire + "秒");
        return Result.success(result);
    }
}

