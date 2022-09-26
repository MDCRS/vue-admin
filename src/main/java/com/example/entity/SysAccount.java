package com.example.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.Data;

/**
 * @TableName sys_account
 */
@TableName(value = "sys_account")
@Data
public class SysAccount implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     *
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /**
     *
     */
    private Long userId;
    /**
     * 当前总金额
     */
    private Double totalAmount;
    /**
     * 余额
     */
    private Double balance;
    /**
     * 历史总充值金额
     */
    private Double hostoryAmount;
    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer isDeleted;
    /**
     * 添加时间
     */
    private LocalDateTime created;
    /**
     * 修改时间
     */
    private LocalDateTime updated;
}