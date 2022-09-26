package com.example.controller;

import cn.hutool.core.map.MapUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.common.dto.LoginUserInfoDto;
import com.example.common.exception.CaptchaException;
import com.example.common.lang.Constant;
import com.example.common.lang.Result;
import com.example.entity.*;
import com.example.mapper.SysLevelMapper;
import com.example.utils.SMSUtil;
import com.example.utils.StrUtils;
import com.google.code.kaptcha.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@RestController
public class AuthController extends BaseController {

    @Autowired
    private Producer producer;

    @Autowired
    private SysLevelMapper sysLevelMapper;


    @GetMapping("/captcha")
    public Result captcha() throws IOException {
        String key = UUID.randomUUID().toString();
        String code = producer.createText();
        BufferedImage image = producer.createImage(code);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpeg", outputStream);
        BASE64Encoder encoder = new BASE64Encoder();
        String str = "data:image/jpeg;base64,";
        String base64Img = str + encoder.encode(outputStream.toByteArray());
        redisUtil.hset(Constant.CAPTCHA_KEY, key, code, 120);
        return Result.success(
                MapUtil.builder()
                        .put("token", key)
                        .put("captchaImg", base64Img)
                        .build());
    }

    @GetMapping("/sys/userInfo")
    public Result userInfo(Principal principal) {
        System.out.println("principal.getName() = " + principal.getName());
        boolean number = StrUtils.isNumber(principal.getName());
        SysUser user = null;
        if (number) {
            user = sysUserService.getUserByPhone(principal.getName());
        } else {
            user = sysUserService.getByUsername(principal.getName());
        }
        LoginUserInfoDto loginUserInfoDto = convertUserInfo(user);
        return Result.success(loginUserInfoDto);
    }

    @GetMapping("/oss/policy")
    public Map<String, String> policy() {
        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        String accessId = "LTAI5tFj8u1ZeAUoxkoXHXmo";
        String accessKey = "yiaM5p5yzZTXyWUJ7nzQGjoO4iZ0UB";
        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
        String endpoint = "oss-cn-beijing.aliyuncs.com";
        // 填写Bucket名称，例如examplebucket。
        String bucket = "lqz-1";
        // 填写Host名称，格式为https://bucketname.endpoint。
        String host = "https://" + bucket + "." + endpoint;
        // 设置上传到OSS文件的前缀，可置空此项。置空后，文件将上传至Bucket的根目录下。
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String format = simpleDateFormat.format(new Date());
        String dir = "bicycle_test/" + format + "/";

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessId, accessKey);
        try {
            long expireTime = 30;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            // PostObject请求最大可支持的文件大小为5 GB，即CONTENT_LENGTH_RANGE为5*1024*1024*1024。
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes(StandardCharsets.UTF_8);
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = ossClient.calculatePostSignature(postPolicy);//  Signature:签名

            Map<String, String> respMap = new LinkedHashMap<String, String>();
            respMap.put("accessid", accessId);
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", dir);
            respMap.put("host", host);
            respMap.put("expire", String.valueOf(expireEndTime / 1000));
            // respMap.put("expire", formatISO8601Date(expiration));
            return respMap;
        } catch (Exception e) {
            // Assert.fail(e.getMessage());
            System.out.println(e.getMessage());
        } finally {
            ossClient.shutdown();
        }
        return null;
    }

    //点击发送验证码
    @PostMapping("/sendMs/{phone}")
    @ResponseBody
    public Result sendMs(@PathVariable String phone) {
        if (phone != null && !phone.equals("")) {
            Result result = SMSUtil.sendSMS(phone, redisUtil);
            System.out.println("sendMs->result = " + result.getData());
            return result;
        } else {
            return Result.fail("短信发送失败！");
        }
    }

    //点击发送登录验证码
    @GetMapping("/sendLoginMs/{phone}")
    @ResponseBody
    public Result sendLoginMs(@PathVariable String phone) {
        SMSUtil smsUtil = new SMSUtil();
        if (phone != null && !phone.equals("")) {
            return smsUtil.sendSMSLogin(phone, redisUtil);
        } else {
            return Result.fail("短信发送失败！");
        }
    }

    //注册验证
    @PostMapping("/register")
    @ResponseBody
    public Result register(HttpServletRequest request) {
        validate(request);
        String avatar = request.getParameter("avatar");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String phone = request.getParameter("phone");
        SysUser sysUser = new SysUser();
        sysUser.setAvatar(avatar);
        sysUser.setUsername(username);
        sysUser.setPhone(phone);
        String encode = passwordEncoder.encode(password);
        sysUser.setPassword(encode);
        sysUser.setStatus(1);
        sysUser.setCreated(LocalDateTime.now());
        boolean save = sysUserService.save(sysUser);
        //给新注册的用户初始化角色,为普通用户
        SysUserRole sysUserRole = new SysUserRole();
        sysUserRole.setUserId(sysUser.getId());
        sysUserRole.setRoleId(3L);
        boolean save1 = sysUserRoleService.save(sysUserRole);
        //初始化新用户的账户
        SysAccount sysAccount = new SysAccount();
        sysAccount.setBalance(0.0);
        sysAccount.setTotalAmount(0.0);
        sysAccount.setCreated(LocalDateTime.now());
        sysAccount.setUserId(sysUser.getId());
        sysAccount.setHostoryAmount(0.0);
        boolean save2 = sysAccountService.save(sysAccount);
        String str = save && save1 && save2 ? "注册成功!" : "注册失败!";
        return Result.success(str);
    }


    @PostMapping("/rePassWord")
    @ResponseBody
    public Result rePassWord(HttpServletRequest request) {
        validate(request);
        SysUser user = sysUserService.getByUsername(request.getParameter("username"));
        user.setPassword(passwordEncoder.encode(request.getParameter("newPass")));
        boolean b = sysUserService.updateById(user);
        if (b) {
            return Result.success("修改密码成功！");
        } else {
            return Result.fail("修改密码失败！");
        }
    }

    @GetMapping("/checkUserByName/{username}")
    public Result checkUserByName(@PathVariable String username) {
        SysUser byUsername = sysUserService.getByUsername(username);
        if (!StringUtils.checkValNotNull(byUsername)) {
            return Result.success(200, "找不到该用户，请重新输入或注册", null);
        }
        return Result.success(200, null, null);
    }

    @GetMapping("/checkUserByNameAndPhone/{username}/{phone}")
    public Result checkUserByName(@PathVariable String username, @PathVariable String phone) {
        SysUser byUsername = sysUserService.getByUsernameAndPhone(username, phone);
        if (!StringUtils.checkValNotNull(byUsername)) {
            return Result.success(200, "手机号不正确", null);
        }
        return Result.success(200, null, null);
    }

    @GetMapping("/checkUserName/{username}")
    public Result checkUserName(@PathVariable String username) {
        SysUser byUsername = sysUserService.getByUsername(username);
        if (StringUtils.checkValNotNull(byUsername)) {
            return Result.success(200, "用户名已被使用，请重新输入", true);
        } else {
            return Result.success(false);
        }
    }

    @GetMapping("/checkPhone/{phone}")
    public Result checkPhone(@PathVariable("phone") String phone) {
        SysUser byUsername = sysUserService.getUserByPhone(phone);
        if (StringUtils.checkValNotNull(byUsername)) {
            return Result.success(200, "该手机号已被使用，请重新输入", true);
        } else {
            return Result.success(false);
        }
    }


    private void validate(HttpServletRequest request) {
        String code = request.getParameter("code");
        System.out.println("code = " + code);
        String key = request.getParameter("key");
        System.out.println("key = " + key);
        if (StringUtils.isBlank(code) || StringUtils.isBlank(key)) {
            throw new CaptchaException("验证码不能为空！");
        }
        if (!code.equals(redisUtil.hget(Constant.REG_CODE, key))) {
            throw new CaptchaException("验证码错误！");
        }
        redisUtil.hdel(Constant.REG_CODE, key);
    }

    private LoginUserInfoDto convertUserInfo(SysUser sysUser) {
        LoginUserInfoDto loginUserInfoDto = new LoginUserInfoDto();
        loginUserInfoDto.setAvatar(sysUser.getAvatar());
        loginUserInfoDto.setUsername(sysUser.getUsername());
        loginUserInfoDto.setLastLogin(sysUser.getLastLogin());
        loginUserInfoDto.setPhone(sysUser.getPhone());
        loginUserInfoDto.setStatus(sysUser.getStatus());
        loginUserInfoDto.setCreditValue(sysUser.getCreditValue());
        loginUserInfoDto.setId(sysUser.getId());
        List<SysRole> roles = sysRoleService.listRolesByUserId(sysUser.getId());
        SysAccount sysAccount = sysAccountService.getOne(new QueryWrapper<SysAccount>().eq("user_id", sysUser.getId()));
        loginUserInfoDto.setSysAccount(sysAccount);
        double amount = sysAccount.getTotalAmount();
        if (amount >= 0 && amount < 500) {
            sysUser.setLevelId(1L);
        } else if (amount >= 500 && amount < 1000) {
            sysUser.setLevelId(2L);
        } else if (amount >= 1000 && amount < 2000) {
            sysUser.setLevelId(3L);
        } else if (amount >= 2000 && amount < 3000) {
            sysUser.setLevelId(4L);
        } else {
            sysUser.setLevelId(5L);
        }
        sysUserService.updateById(sysUser);
        SysUser sysUser1 = sysUserService.getById(sysUser.getId());
        loginUserInfoDto.setSysLevel(sysLevelMapper.selectById(sysUser1.getLevelId()));
        loginUserInfoDto.setRoles(roles);
        return loginUserInfoDto;
    }
}
