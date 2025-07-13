package com.kob.kobbackend.utils;

import com.kob.kobbackend.pojo.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserUtil {
    public static User getUser() {
        // 返回当前用户的身份验证对象
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        // 获取主体对象
        UserDetailsimpl loginUser = (UserDetailsimpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();
        return  user;
    }
}
