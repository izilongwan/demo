package com.demo.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.mapper.EmployeeMapper;
import com.demo.pojo.entity.Employee;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements IService<Employee> {

}
