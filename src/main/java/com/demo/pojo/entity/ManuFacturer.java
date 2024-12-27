package com.demo.pojo.entity;

import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;

import lombok.Data;

@Data
public class ManuFacturer {
    Integer id;
    String name;
    String nameEn;
    Integer mId;
    String city;

    @TableField(exist = false)
    Car car;

    @TableField(exist = false)
    // mybatis collection只能组装List集合
    List<Car> cars;
}
