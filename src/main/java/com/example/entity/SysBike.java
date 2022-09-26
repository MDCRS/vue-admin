package com.example.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.hibernate.validator.internal.metadata.aggregated.rule.OverridingMethodMustNotAlterParameterConstraints;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @TableName sys_bike
 */
@TableName(value = "sys_bike")
@Data
public class SysBike implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 车辆名称
     */
    private String bikeName;
    /**
     * 自行车品牌
     */
    private String brand;
    /**
     * 车辆描述
     */
    @NotBlank(message = "描述不能为空")
    private String des;
    /**
     * 自行车图片
     */
    private String image;
    /**
     * 自行车停放位置
     */
    private String address;
    /**
     * 自行车租金
     */
    private Double rentPrice;
    private Double dayPrice;
    @TableField(exist = false)
    private String hostName;
    /**
     * 自行车租借次数
     */
    private Long rentCount;
    /**
     * 当前租车人的联系方式
     */
    private String rentPhone;
    /**
     * 当前租车人的用户名
     */
    private String rentName;
    /**
     * 押金
     */
    private Double deposit;
    /**
     * 自行车状态
     */
    private Integer bikeStatus;
    /**
     * 自行车支付状态
     */
    private Integer isPay;
    /**
     * 租车状态
     */
    private Integer rentStatus;
    /**
     * 车主是否同意出租
     */
    private Integer isAgree;
    /**
     * 添加时间
     */
    private LocalDateTime created;
    private String endDate;
    /**
     * 修改时间
     */
    private LocalDateTime updated;
    /**
     * 车主id
     */
    private Long userId;
    /**
     * 逻辑删除字段
     */
    @TableLogic
    private Integer isDeleted;

}