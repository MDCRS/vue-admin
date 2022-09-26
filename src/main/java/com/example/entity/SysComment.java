package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * @TableName sys_comment
 */
@TableName(value = "sys_comment")
@Data
public class SysComment implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 父级id
     */
    private Long parentId;
    /**
     * 被评价自行车id
     */
    private Long bikeId;
    /**
     * 评价用户id
     */
    private Long userId;
    /**
     * 评价内容
     */
    private String contain;
    /**
     *
     */
    private Integer isDeleted;
    /**
     *
     */
    private Date created;
    /**
     *
     */
    private Date updated;
}