package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.Data;
import org.apache.tomcat.jni.Local;

/**
 * @TableName sys_profit
 */
@TableName(value = "sys_profit")
@Data
public class SysProfit implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 总收益
     */
    private Double account;
    /**
     *
     */
    private LocalDateTime created;
    /**
     *
     */
    private LocalDateTime updated;
}