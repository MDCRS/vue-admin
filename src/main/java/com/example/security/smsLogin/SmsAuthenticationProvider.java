package com.example.security.smsLogin;

import cn.hutool.core.util.StrUtil;
import com.example.common.lang.Constant;
import com.example.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Component
public class SmsAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsServiceImpl;

    private final RedisUtil redisUtil;

    public SmsAuthenticationProvider(@Qualifier("smsUserDetailsService") UserDetailsService userDetailsServiceImpl, RedisUtil redisUtil) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.redisUtil = redisUtil;
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        SmsCodeAuthenticationToken authenticationToken = (SmsCodeAuthenticationToken) authentication;
        Object principal = authentication.getPrincipal();// 获取凭证也就是用户的手机号
        String phone = "";
        if (principal instanceof String) {
            phone = (String) principal;
        }
        String inputCode = (String) authentication.getCredentials(); // 获取输入的验证码
        // 1. 检验Redis手机号的验证码
//        String redisCode = redisTemplate.opsForValue().get(phone);
        String redisCode = (String) redisUtil.get(phone);
        if (StrUtil.isEmpty(redisCode)) {
            throw new BadCredentialsException("验证码已经过期或尚未发送，请重新发送验证码");
        }
        if (!inputCode.equals(redisCode)) {
            throw new BadCredentialsException("输入的验证码不正确，请重新输入");
        }
        // 2. 根据手机号查询用户信息
        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(phone);
        if (userDetails == null) {
            throw new InternalAuthenticationServiceException("phone用户不存在，请注册");
        }
        // 3. 重新创建已认证对象,
        SmsCodeAuthenticationToken authenticationResult = new SmsCodeAuthenticationToken(principal, inputCode, userDetails.getAuthorities());
        authenticationResult.setDetails(authenticationToken.getDetails());
        return authenticationResult;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return SmsCodeAuthenticationToken.class.isAssignableFrom(aClass);
    }
}
