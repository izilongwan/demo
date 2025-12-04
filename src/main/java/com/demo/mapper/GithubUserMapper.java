package com.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.domain.entity.GithubUser;

public interface GithubUserMapper extends BaseMapper<GithubUser> {
    GithubUser selectGithubUserById(String id);
}
