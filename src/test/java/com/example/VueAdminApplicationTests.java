package com.example;

import com.example.entity.SysBike;
import com.example.entity.SysRentBike;
import com.example.entity.SysUser;
import com.example.service.SysBikeService;
import com.example.service.SysRentBikeService;
import com.example.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@Slf4j
class VueAdminApplicationTests {

    @Resource
    private Environment config;


    @Resource
    private SysRentBikeService sysRentBikeService;

    @Resource
    private SysUserService sysUserService;

    @Resource
    private SysBikeService sysBikeService;

    @Test
    public void testAlipayConfig() {
        log.info(config.getProperty("alipay.app-id"));
        log.info(config.getProperty("alipay.alipay-public-key"));
    }


    @Test
    public void test1() {
        List<SysBike> list = sysBikeService.getAll();
        System.out.println("list.size() = " + list.size());
    }

}
