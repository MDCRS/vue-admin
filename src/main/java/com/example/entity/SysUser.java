package com.example.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * <p>
 *
 * </p>
 *
 * @author lqz
 * @since 2022-03-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_user")
public class SysUser extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "用户名不能为空！")
    private String username;

    @NotBlank(message = "密码不能为空！")
    private String password;

    private String avatar;

    private Long levelId;

    private Integer creditValue;

    @NotBlank(message = "手机号不能为空！")
    private String phone;

    private LocalDateTime lastLogin;


    @TableField(exist = false)
    private List<SysRole> sysRoles = new ArrayList<>();


}
