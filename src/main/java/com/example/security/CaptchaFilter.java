package com.example.security;

import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.common.exception.CaptchaException;
import com.example.common.lang.Constant;
import com.example.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CaptchaFilter extends OncePerRequestFilter {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private LoginFailureHandler loginFailureHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        String url = httpServletRequest.getRequestURI();
        System.out.println("idea->CaptchaFilter ----> url = " + url);
        if ("/login".equals(url) && httpServletRequest.getMethod().equals("POST")) {
            try {
                validate(httpServletRequest);
            } catch (CaptchaException e) {
                loginFailureHandler.onAuthenticationFailure(httpServletRequest, httpServletResponse, e);
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private void validate(HttpServletRequest request) {
        String code = request.getParameter("code");
        System.out.println("code = " + code);
        String key = request.getParameter("token");
        System.out.println("key = " + key);
        if (StringUtils.isBlank(code) || StringUtils.isBlank(key)) {
            throw new CaptchaException("验证码不能为空！");
        }
        if (!code.equals(redisUtil.hget(Constant.CAPTCHA_KEY, key))) {
            System.out.println("key = " + key);
            throw new CaptchaException("验证码错误！");
        }
        redisUtil.hdel(Constant.CAPTCHA_KEY, key);
    }
}
