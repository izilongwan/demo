package com.demo.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Employee {
    @TableId
    String empId;
    String empName;
    Integer deptId;
    Integer manager;
    Integer jobId;
    String sex;
}
