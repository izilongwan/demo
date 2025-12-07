package com.demo.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mico.app.database.entity.Base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("t_web_report")
public class WebReport extends Base {
    @TableId(type = IdType.ASSIGN_UUID)
    String id;

    String reportName;

    String reportContent;

    String reportType;

    String reportFilename;

    String reportMessage;

    String reportError;
}
