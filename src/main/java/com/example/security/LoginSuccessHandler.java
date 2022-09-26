package com.example.security;

import cn.hutool.json.JSONUtil;
import com.example.common.lang.Result;
import com.example.entity.SysUser;
import com.example.service.SysUserService;
import com.example.utils.JwtUtils;
import com.example.utils.StrUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    SysUserService sysUserService;
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        ServletOutputStream outputStream = httpServletResponse.getOutputStream();
        //生成jwt并放到请求头中
        String jwt = jwtUtils.generateToken(authentication.getName());
        System.out.println("authentication.getName() = " + authentication.getName());
        boolean number = StrUtils.isNumber(authentication.getName());
        System.out.println("number = " + number);
        SysUser user = null;
        if (number) {
            user = sysUserService.getUserByPhone(authentication.getName());
        } else {
            user = sysUserService.getByUsername(authentication.getName());
        }
        user.setLastLogin(LocalDateTime.now());
        if (user.getCreditValue() <= 0) {
            user.setStatus(0);
            user.setCreditValue(user.getCreditValue() + 50);
        }
        sysUserService.updateById(user);
        if (number) {
            user = sysUserService.getUserByPhone(authentication.getName());
        } else {
            user = sysUserService.getByUsername(authentication.getName());
        }
        Result result = null;
        Map<String, Object> resMap = new HashMap<>();
        httpServletResponse.setHeader(jwtUtils.getHeaderJwt(), jwt);
        if (user.getStatus() == 0) {
            resMap.put("resCode", 3000);
            resMap.put("resMsg", "登录成功，但已被锁定，不能进行租车，请联系管理员");
            result = Result.success(resMap);
            outputStream.write(JSONUtil.toJsonStr(result).getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            outputStream.close();
            return;
        }
        resMap.put("resCode", 2000);
        resMap.put("resMsg", "登录成功!");
        result = Result.success(resMap);
        outputStream.write(JSONUtil.toJsonStr(result).getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();
    }
}
