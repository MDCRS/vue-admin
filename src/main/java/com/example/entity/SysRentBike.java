package com.example.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @TableName sys_rent_bike
 */
@TableName(value = "sys_rent_bike")
@Data
public class SysRentBike implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 租出id
     */
    @NotNull
    private Long rentId;
    /**
     * 车主id
     */
    private Long hostId;
    /**
     * 自行车id
     */
    @NotNull
    private Long bikeId;
    /**
     * 是否支付租单
     */
    private Integer isPay;
    /**
     * 租车开始时间
     */
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH")
    private LocalDateTime startDate;
    /**
     * 租车结束时间
     */
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH")
    private LocalDateTime endDate;
    /**
     * 还车时间
     */
    private LocalDateTime returnDate;
    /**
     * 租金
     */
    private Double rentPrice;
    /**
     * 记录创建时间
     */
    private LocalDateTime created;
    /**
     * 记录修改时间
     */
    private LocalDateTime updated;
    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer isDeleted;
    private Integer agreement;
    private Integer isAgree;


}