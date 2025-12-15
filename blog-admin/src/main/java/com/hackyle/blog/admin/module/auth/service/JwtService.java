package com.hackyle.blog.admin.module.auth.service;


import com.hackyle.blog.admin.module.auth.model.dto.UserDetailsDto;
import io.jsonwebtoken.Claims;

import javax.servlet.http.HttpServletRequest;

public interface JwtService {

    String createJWT(UserDetailsDto userDetailsDto);

    UserDetailsDto getUserDetails(HttpServletRequest request);

    String getToken(HttpServletRequest request);

    String refreshToken(String oldToken, Claims claims);

    String refreshToken(HttpServletRequest request);
}
