package com.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.domain.entity.Car;
import com.demo.domain.entity.ManuFacturer;

public interface CarMapper extends BaseMapper<Car> {
    ManuFacturer[] selectManuFacturers2();

    ManuFacturer[] selectManuFacturers();
}
