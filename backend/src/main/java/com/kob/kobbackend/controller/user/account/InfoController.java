package com.kob.kobbackend.controller.user.account;

import com.kob.kobbackend.service.user.account.InfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
public class InfoController {
    @Resource
    private InfoService infoService;
    @GetMapping("/user/account/info")
    public Map<String, String> getUserInfo() {
        return infoService.getUserInfo();
    }
}
