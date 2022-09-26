package com.example.common.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;


@Data
public class BikeDto {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
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
     * 自行车租金
     */
    private Double rentPrice;

    /**
     * 自行车租借次数
     */
    private Long rentCount;

    /**
     * 当前租车人的联系方式
     */
    private String rentPhone;

    /**
     * 押金
     */
    private Double deposit;

    /**
     * 自行车状态
     */
    private Integer bikeStatus;

    /**
     * 租车状态
     */
    private Integer rentStatus;

    /**
     * 添加时间
     */
    private LocalDateTime created;

    /**
     * 修改时间
     */
    private LocalDateTime updated;

    /**
     * 车主id
     */
    private Long userId;

}
