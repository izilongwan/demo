package com.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.demo.domain.entity.GithubUser;

public interface GithubUserService extends IService<GithubUser> {
    public GithubUser getGithubUserById(String id);
}
