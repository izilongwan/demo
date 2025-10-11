package com.demo.domain.vo;

import java.util.List;

import com.demo.domain.entity.Employee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeVO extends Employee {
    List<Employee> list;
}
