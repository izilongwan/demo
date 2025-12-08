package com.demo.domain.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(description = "汽车实体")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Car implements Serializable {
    Integer id;

    @ApiModelProperty("名称")
    String name;

    @ApiModelProperty("长度")
    Double lang;

    String value;

    Integer colorId;

    @TableField(exist = false)
    String colorNameCn;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    LocalDateTime updateTime;
}
