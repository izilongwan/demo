package com.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.entity.Car;
import com.demo.entity.ManuFacturer;

public interface CarMapper extends BaseMapper<Car> {
    ManuFacturer[] selectManuFacturers2();

    ManuFacturer[] selectManuFacturers();
}
