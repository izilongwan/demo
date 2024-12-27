package com.demo.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Employee {
    String empId;
    String empName;
    Integer deptId;
    Integer manager;
    Integer jobId;
    String sex;
}
