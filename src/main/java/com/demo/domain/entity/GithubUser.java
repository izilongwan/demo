package com.demo.domain.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("t_github_user")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GithubUser {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @TableField("github_id")
    private Long githubId;

    @TableField("login_username")
    private String loginUsername;

    @TableField("avatar_url")
    private String avatarUrl;

    @TableField("node_id")
    private String nodeId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
