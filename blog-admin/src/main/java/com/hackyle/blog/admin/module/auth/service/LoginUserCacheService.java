package com.hackyle.blog.admin.module.auth.service;

import com.hackyle.blog.admin.module.auth.model.dto.UserDetailsDto;

import java.util.List;

public interface LoginUserCacheService {
    void putCache(UserDetailsDto userDetails);

    void refreshCache(UserDetailsDto userDetailsDto);

    UserDetailsDto getCache(Long userId);

    boolean removeCache(Long userId);

    List<UserDetailsDto> getAll();

}
