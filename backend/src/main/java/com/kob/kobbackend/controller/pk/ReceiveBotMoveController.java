package com.kob.kobbackend.controller.pk;

import com.kob.kobbackend.service.pk.ReceiveBotMoveService;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class ReceiveBotMoveController {
    @Resource
    private ReceiveBotMoveService receiveBotMoveService;

    @PostMapping("/pk/receive/bot/move")
    public String receiveBotMove(@RequestBody MultiValueMap<String, String > data) {
        Integer userId = Integer.valueOf(data.getFirst("user_id"));
        Integer direction = Integer.valueOf(data.getFirst("direction"));
        return receiveBotMoveService.receiveBotMove(userId, direction);
    }
}
