package com.demo.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mico.app.database.entity.Base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@TableName("t_github_user")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GithubUser extends Base {

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
}
