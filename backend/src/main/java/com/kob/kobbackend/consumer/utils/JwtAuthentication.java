package com.kob.kobbackend.consumer.utils;

import com.kob.kobbackend.utils.JwtUtil;
import io.jsonwebtoken.Claims;

/**
 * 将令牌映射为用户id
 */
public class JwtAuthentication {
    public static Integer getUserId(String token) {
        int userId = -1;
        Claims claims = null;
        try {
            claims = JwtUtil.parseJWT(token);
            userId = Integer.parseInt(claims.getSubject());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return userId;
    }
}
