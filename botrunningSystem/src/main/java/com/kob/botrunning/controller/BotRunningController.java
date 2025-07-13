package com.kob.botrunning.controller;

import com.kob.botrunning.service.BotRunningService;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class BotRunningController {
    @Resource
    private BotRunningService botRunningService;

    @PostMapping("/bot/add")
    public String addBot(@RequestBody MultiValueMap<String, String> data) {
        Integer userId = Integer.parseInt(data.getFirst("user_id"));
        String botCode = data.getFirst("bot_code");
        String input = data.getFirst("input");
        return botRunningService.addBot(userId, botCode, input);
    }

}
