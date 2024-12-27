package com.demo.mapper;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmployeeMapperTest {

    @Resource
    EmployeeMapper employeeMapper;

    @Test
    public void getList() {
        employeeMapper.selectList(null);
    }
}
