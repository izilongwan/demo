package com.demo.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.domain.entity.GithubUser;
import com.demo.mapper.GithubUserMapper;
import com.demo.service.GithubUserService;

@Service
public class GithubUserServiceImpl extends ServiceImpl<GithubUserMapper, GithubUser> implements GithubUserService {

    private final GithubUserMapper githubUserMapper;

    public GithubUserServiceImpl(GithubUserMapper githubUserMapper) {
        this.githubUserMapper = githubUserMapper;
    }

    public GithubUser getGithubUserById(Long id) {
        return new LambdaQueryChainWrapper<GithubUser>(githubUserMapper)
                .select(GithubUser::getId,
                        GithubUser::getLoginUsername,
                        GithubUser::getAvatarUrl,
                        GithubUser::getGithubId,
                        GithubUser::getNodeId)
                .eq(GithubUser::getId, id)
                .one();
    }
}
