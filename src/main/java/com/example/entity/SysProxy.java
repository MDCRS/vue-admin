package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * @TableName sys_proxy
 */
@TableName(value = "sys_proxy")
@Data
public class SysProxy implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 条款内容
     */
    private String content;
    /**
     *
     */
    private Date created;
    /**
     *
     */
    private Date updated;
    /**
     *
     */
    private Integer isDeleted;
}