package com.example.security.smsLogin;

import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SmsAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    // 设置拦截/sms/login短信登录接口
    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/sms/login", "POST");
    // 认证参数
    private String phoneParameter = "phone";
    private String smsCodeParameter = "smsCode";
    private boolean postOnly = true;

    public SmsAuthenticationFilter() {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER);
    }

    public SmsAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (this.postOnly && !"POST".equals(request.getMethod())) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        } else {
            String phone = this.obtainPhone(request);
            phone = phone != null ? phone : "";
            phone = phone.trim();
            String smsCode = this.obtainSmsCode(request);
            smsCode = smsCode != null ? smsCode : "";
            SmsCodeAuthenticationToken authRequest = new SmsCodeAuthenticationToken(phone, smsCode);
            this.setDetails(request, authRequest);
            // 认证
            return this.getAuthenticationManager().authenticate(authRequest);
        }
    }

    @Nullable
    protected String obtainSmsCode(HttpServletRequest request) {
        return request.getParameter(this.smsCodeParameter);
    }

    @Nullable
    protected String obtainPhone(HttpServletRequest request) {
        return request.getParameter(this.phoneParameter);
    }

    protected void setDetails(HttpServletRequest request, SmsCodeAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

    public void setPhoneParameter(String phoneParameter) {
        Assert.hasText(phoneParameter, "Phone parameter must not be empty or null");
        this.phoneParameter = phoneParameter;
    }

    public void setSmsCodeParameter(String smsCodeParameter) {
        Assert.hasText(smsCodeParameter, "SmsCode parameter must not be empty or null");
        this.smsCodeParameter = smsCodeParameter;
    }

    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }

    public final String getUsernameParameter() {
        return this.phoneParameter;
    }

    public final String getPasswordParameter() {
        return this.smsCodeParameter;
    }
}

