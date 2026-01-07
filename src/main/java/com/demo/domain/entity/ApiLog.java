package com.demo.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@TableName("t_api_log")
@NoArgsConstructor
@AllArgsConstructor
public class ApiLog {
    @TableId
    private Long id;
    private String user;
    private String method;
    private String uri;
    private String ip;
    private String requestParams;
    private String responseParams;
    private String createTime;
}
