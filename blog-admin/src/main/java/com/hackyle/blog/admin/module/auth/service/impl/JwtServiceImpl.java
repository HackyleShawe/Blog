package com.hackyle.blog.admin.module.auth.service.impl;

import com.hackyle.blog.admin.module.auth.model.dto.UserDetailsDto;
import com.hackyle.blog.admin.module.auth.service.JwtService;
import com.hackyle.blog.admin.module.auth.service.LoginUserCacheService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class JwtServiceImpl implements JwtService {
    @Value("${auth.token.header}")
    private String tokenHeader;

    @Value("${auth.token.secret}")
    private String tokenSecret;

    @Value("${auth.token.expireMinutes}")
    private String tokenExpireMinutes;

    @Autowired
    private LoginUserCacheService loginUserCacheService;

    /**
     * userId的Key
     */
    public static final String AUTHORITIES_UID_KEY = "userId";
    //20分钟
    private static final Long MILLIS_MINUTE_TWENTY = 20 * 60 * 1000L;

    /** JWT的header内容 */
    private final Map<String, Object> jwtHeader = new HashMap<>();

    public JwtServiceImpl() {
        jwtHeader.put("alg", "HS256"); //alg属性表示签名的算法（algorithm），默认是 HMAC SHA256（写成 HS256）
        jwtHeader.put("typ", "JWT"); //typ属性表示这个令牌（token）的类型（type），JWT 令牌统一写为JWT
    }

    /**
     * 依次构造JWT的三大组成部分：Header，Payload，Signature，调用API生成Token
     */
    public String createJWT(UserDetailsDto userDetailsDto) {
        Date now = new Date();//当前时间
        JwtBuilder jwtBuilder = Jwts.builder();
        //JWT的Token体第一部分: Header
        //jwtBuilder.setHeader(header);

        //JWT的Token体第二部分: Payload
        jwtBuilder.setId(UUID.randomUUID().toString()) //编号
                .setSubject(userDetailsDto.getUsername()) //主体
                //.setAudience(jwtPayloadDto.getAudience()) //受众
                //.setIssuer(userDetailsDto.getUsername()) //签发人
                .setIssuedAt(now) //签发时间
                .setExpiration(new Date(now.getTime() + (Long.parseLong(tokenExpireMinutes) * 60 * 1000))); //设置过期时间
        //Payload中的其他数据
        Map<String, Object> payloadExtendMap = new HashMap<>();
        payloadExtendMap.put(AUTHORITIES_UID_KEY, userDetailsDto.getUserId());
        jwtBuilder.addClaims(payloadExtendMap);
        //jwtBuilder.setClaims(payloadExtendMap); 注意，这样会清空原来的claims

        //JWT的Token体第三部分: Signature
        jwtBuilder.signWith(SignatureAlgorithm.HS512, tokenSecret);

        return jwtBuilder.compact(); //生成Token
    }


    /**
     * 从JWT中解析出uid，再从缓存中拿取对应的登录态数据，便于后续放在请求上下文中
     * @param request HttpServletRequest
     * @return UserDetailsDto
     */
    public UserDetailsDto getUserDetails(HttpServletRequest request) {
        String token = getToken(request);
        if(StringUtils.isBlank(token)) {
            return null;
        }

        Object userId = null;
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSecret).parseClaimsJws(token);
            Claims body = claimsJws.getBody();
            userId = body.get(AUTHORITIES_UID_KEY);
        } catch (Exception e) {
            log.error("token解析异常:", e);
            //throw new BadCredentialsException(e.getMessage());
        }

        if(userId == null) {
            return null;
        }
        UserDetailsDto userDetailsDto = loginUserCacheService.getCache(Long.parseLong(userId.toString()));
        if(userDetailsDto != null) {
            loginUserCacheService.refreshCache(userDetailsDto);
        }

        return userDetailsDto;
    }


    /**
     * 从HTTP请求中获取JWT
     */
    public String getToken(HttpServletRequest request) {
        String token = request.getHeader(tokenHeader);
        token = StringUtils.isBlank(token) ? request.getHeader("Authorization") : token;
        token = StringUtils.isBlank(token) ? request.getHeader("X-Token") : token;
        token = StringUtils.isBlank(token) ? request.getParameter("token") : token;

        if (StringUtils.isNotEmpty(token) && token.startsWith("Bearer ")) {
            token = token.replace("Bearer ", "");
        }
        return token;
    }



    /**
     * 检查是否Token是否已经临近失效
     * 如果是则更新Token，否则返回原Token
     */
    public String refreshToken(String oldToken, Claims claims) {
        String newToken = oldToken;

        Date expiration = claims.getExpiration();//过期时间
        Date now = new Date();

        //已经符合过期时间的设定了
        if (expiration.getTime() - now.getTime() < MILLIS_MINUTE_TWENTY) {
            //重新设置签发时间和过期时间
            claims.setIssuedAt(now);
            claims.setExpiration(new Date(now.getTime() + (Long.parseLong(tokenExpireMinutes) * 60 * 1000)));

            JwtBuilder jwtBuilder = Jwts.builder();
            //jwtBuilder.setHeader(jwtHeader); //JWT的Token体第一部分: Header
            jwtBuilder.setClaims(claims); //JWT的Token体第二部分：payload
            jwtBuilder.signWith(SignatureAlgorithm.HS512, tokenSecret); //JWT的Token的第三部分：Signature

            newToken = jwtBuilder.compact(); //在原来Token的基础上，重新设置了签发时间和过期时间后，重新生成Token
        }

        return newToken;
    }

    public String refreshToken(HttpServletRequest request) {
        String token = getToken(request);
        if(StringUtils.isBlank(token)) {
            return token;
        }

        String newToken = token;
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSecret).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();

        Date expiration = claims.getExpiration();//过期时间
        Date now = new Date();

        //已经符合过期时间的设定了
        if (expiration.getTime() - now.getTime() < MILLIS_MINUTE_TWENTY) {
            //重新设置签发时间和过期时间
            claims.setIssuedAt(now);
            claims.setExpiration(new Date(now.getTime() + (Long.parseLong(tokenExpireMinutes) * 60 * 1000)));

            JwtBuilder jwtBuilder = Jwts.builder();
            //jwtBuilder.setHeader(jwtHeader); //JWT的Token体第一部分: Header
            jwtBuilder.setClaims(claims); //JWT的Token体第二部分：payload
            jwtBuilder.signWith(SignatureAlgorithm.HS512, tokenSecret); //JWT的Token的第三部分：Signature

            newToken = jwtBuilder.compact(); //在原来Token的基础上，重新设置了签发时间和过期时间后，重新生成Token
        }

        return newToken;
    }

}
