package com.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.entity.Car;
import com.entity.ManuFacturer;

public interface CarMapper extends BaseMapper<Car> {
    ManuFacturer[] selectManuFacturers2();

    ManuFacturer[] selectManuFacturers();
}
