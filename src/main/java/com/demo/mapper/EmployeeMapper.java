package com.demo.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.MapKey;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.domain.entity.Employee;

public interface EmployeeMapper extends BaseMapper<Employee> {
    @MapKey("empId")
    Map<String, Employee> selectIdMap();

    @MapKey("deptId")
    Map<String, List<Employee>> selectDeptMap();
}
