package com.example.security.smsLogin;

import com.example.entity.SysUser;
import com.example.security.AccountUser;
import com.example.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service("smsUserDetailsService")
public class SmsUserDetailsService implements UserDetailsService {

    @Resource
    SysUserService sysUserService;

    /**
     * @param phone 手机号
     */
    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        // 1. 数据库查询手机用户,这里需要写根据手机号查询用户信息，这里写死吧。。。
        SysUser sysUser = sysUserService.getUserByPhone(phone);
        if (sysUser == null) {
            throw new UsernameNotFoundException("手机号不存在！");
        }
        // 2. 设置权限集合，后续需要数据库查询
        return new AccountUser(sysUser.getId(), sysUser.getUsername(), sysUser.getPassword(), getUserAuthority(sysUser.getId()));
    }

    public List<GrantedAuthority> getUserAuthority(Long userId) {

        String authority = sysUserService.getUserAuthorityInfo(userId);
        return AuthorityUtils.commaSeparatedStringToAuthorityList(authority);
    }
}

