package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.Data;

/**
 * @TableName sys_charge_record
 */
@TableName(value = "sys_charge_record")
@Data
public class SysChargeRecord implements Serializable {


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 充值用户id
     */
    private Long userId;
    /**
     * 充值标题
     */
    private String title;
    /**
     * 充值金额
     */
    private Double amount;
    /**
     * 充值订单号
     */
    private String orderNo;
    /**
     * 充值订单状态
     */
    private String orderStatus;
    /**
     * 充值方式
     */
    private String paymentType;
    /**
     * 充值时间
     */
    private LocalDateTime created;
    /**
     * 修改时间
     */
    private LocalDateTime updated;
    /**
     * 逻辑删除
     */
    private Integer isDeleted;
}