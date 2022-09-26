package com.example.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.function.DoubleBinaryOperator;

import lombok.Data;

/**
 * @TableName sys_pay_record
 */
@TableName(value = "sys_pay_record")
@Data
public class SysPayRecord implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 支付记录的用户id
     */
    private Long rentId;
    /**
     * 车主id
     */
    private Long hostId;
    /**
     * 支付车辆id
     */
    private Long bikeId;
    /**
     * 支付时间
     */
    private LocalDateTime payTime;
    /**
     * 支付金额
     */
    private Double payAmount;
    /**
     * 押金
     */
    private Double deposit;
    /**
     * 记录创建时间
     */
    private LocalDateTime created;
    /**
     * 支付意图
     */
    private String purpose;
    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer isDeleted;
}