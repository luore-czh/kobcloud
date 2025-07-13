package com.kob.kobbackend.controller.user.account;

import com.kob.kobbackend.service.user.account.RegisterService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
public class RegisterController {
    @Resource
    private RegisterService registerService;

    /**
     * 用户注册
     * @param map
     * @return
     */
    @PostMapping ("/user/account/register")
    public Map<String, String> register(@RequestParam Map<String, String> map) {
        String username =  map.get("username");
        String password = map.get("password");
        String confirmedPassword = map.get("confirmedPassword");
        return registerService.register(username, password, confirmedPassword);
    }
}
