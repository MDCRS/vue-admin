package com.example.config;

import com.example.security.*;
import com.example.security.smsLogin.SmsAuthenticationProvider;
import com.example.security.smsLogin.SmsSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private static final String[] URL_WHITELIST = {

            "/login",
            "/logout",
            "/captcha",
            "/favicon.ico",
            "/oss/policy",
            "/sys/bike/list",
            "/sys/bike/homeList",
            "/sys/bike/info/*",
            "/register",
            "/sendMs/*",
            "/sendLoginMs/*",
            "/rePassWord",
            "/checkUserByName/*",
            "/checkUserByNameAndPhone/**",
            "/checkUserName/*",
            "/sys/bikeRent/toCountPrice",
            "/sys/bike/orderBikes",
            "/sys/bike/remark/*",
            "/sys/bike/proxy",
            "/api/ali-pay/trade/notify",
            "/webSocket/**",
            "/sms/send/code/*",
            "/sms/login",
            "/sendLoginMs/*",
            "/checkPhone/*"
    };
    @Autowired
    LoginFailureHandler loginFailureHandler;
    @Autowired
    LoginSuccessHandler loginSuccessHandler;
    @Autowired
    CaptchaFilter captchaFilter;
    @Autowired
    JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @Autowired
    JwtAccessDeniedHandler jwtAccessDeniedHandler;
    @Autowired
    UserDetailServiceImpl userDetailService;
    @Autowired
    JwtLogoutSuccessHandler jwtLogoutSuccessHandler;
    @Autowired
    SmsAuthenticationProvider smsAuthenticationProvider;
    @Autowired
    private SmsSecurityConfigurerAdapter smsSecurityConfigurerAdapter;

    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        return new JwtAuthenticationFilter(authenticationManager());
    }

    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    protected void configure(HttpSecurity http) throws Exception {

        http.cors().and().csrf().disable()

                // 登录配置
                .formLogin()
                .successHandler(loginSuccessHandler)
                .failureHandler(loginFailureHandler)

                .and()
                .logout()
                .logoutSuccessHandler(jwtLogoutSuccessHandler)

                // 禁用session
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                // 配置拦截规则
                .and()
                .authorizeRequests()
                .antMatchers(URL_WHITELIST).permitAll()
                .anyRequest().authenticated()

                // 异常处理器
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                // 配置自定义的过滤器
                .and()
                .addFilter(jwtAuthenticationFilter())
                .addFilterBefore(captchaFilter, UsernamePasswordAuthenticationFilter.class);

        // 添加手机号短信登录
        http.apply(smsSecurityConfigurerAdapter);

    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailService);
    }

    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        ProviderManager authenticationManager = new ProviderManager(Arrays.asList(smsAuthenticationProvider, daoAuthenticationProvider()));
        authenticationManager.setEraseCredentialsAfterAuthentication(false);
        return authenticationManager;
    }

    @Bean
    DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userDetailService);
        daoAuthenticationProvider.setHideUserNotFoundExceptions(false); // 设置显示找不到用户异常
        return daoAuthenticationProvider;
    }
}
