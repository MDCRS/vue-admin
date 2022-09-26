package com.example.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * @TableName sys_level
 */
@TableName(value = "sys_level")
@Data
public class SysLevel implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 用户等级
     */
    private String level;
    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer isDeleted;
    /**
     * 创建时间
     */
    private Date created;
    /**
     * 修改时间
     */
    private Date updated;
}