package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.Data;

/**
 * @TableName sys_remark
 */
@TableName(value = "sys_remark")
@Data
public class SysRemark implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 别评价自行车id
     */
    private Long bikeId;
    /**
     * 评价用户id
     */
    private Long userId;
    @TableField(exist = false)
    private String username;
    @TableField(exist = false)
    private String avatar;
    /**
     * 评价内容
     */
    private String content;
    /**
     * 评价时间
     */
    private LocalDateTime created;
    /**
     * 逻辑删除
     */
    private Integer isDeleted;
}