package com.demo.domain.entity;

import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mico.app.database.entity.Base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@Data
@TableName("t_github_user")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldNameConstants
public class GithubUser extends Base {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("github_id")
    private Long githubId;

    @TableField("login_username")
    private String loginUsername;

    @TableField("avatar_url")
    private String avatarUrl;

    @TableField("node_id")
    private String nodeId;

    @TableField(exist = false)
    private List<String> authorities;

    /**
     * * 逻辑删除字段
     */
    @TableLogic
    Integer deleted;
}
