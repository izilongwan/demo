package com.demo.mapper;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.demo.domain.entity.Employee;

@SpringBootTest
public class EmployeeMapperTest {

    @Resource
    EmployeeMapper employeeMapper;

    @Test
    public void getList() {
        employeeMapper.selectList(null);
    }

    @Test
    public void getIdMap() {
        Map<String, Employee> selectIdMap = employeeMapper.selectIdMap();
        System.out.println(selectIdMap);
    }

    @Test
    public void getDeptMap() {
        Map<String, List<Employee>> selectDeptMap = employeeMapper.selectDeptMap();
        System.out.println(selectDeptMap);
    }
}
