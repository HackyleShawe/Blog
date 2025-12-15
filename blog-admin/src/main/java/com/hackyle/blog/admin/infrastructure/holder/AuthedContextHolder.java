package com.hackyle.blog.admin.infrastructure.holder;

import com.hackyle.blog.admin.module.auth.model.dto.UserDetailsDto;

public class AuthedContextHolder {
    private static final ThreadLocal<UserDetailsDto> USER_DETAILS_DTO_THREAD_LOCAL = new ThreadLocal<>();


    public static void setUserDetailsDto(UserDetailsDto userDetailsDto) {
        USER_DETAILS_DTO_THREAD_LOCAL.set(userDetailsDto);
    }

    public static UserDetailsDto getUserDetailsDto() {
        return USER_DETAILS_DTO_THREAD_LOCAL.get();
    }

    public static void clear() {
        USER_DETAILS_DTO_THREAD_LOCAL.remove();
    }

}
