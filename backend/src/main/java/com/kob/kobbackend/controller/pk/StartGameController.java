package com.kob.kobbackend.controller.pk;

import com.kob.kobbackend.service.pk.StartGameService;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class StartGameController {
    @Resource
    private StartGameService startGameService;

    @PostMapping("/pk/start/game")
    public String startGame(@RequestBody MultiValueMap<String, String> data) {
        Integer aId = Integer.parseInt(data.getFirst("a_id"));
        Integer bId = Integer.parseInt(data.getFirst("b_id"));
        Integer aBotId = Integer.parseInt(data.getFirst("a_bot_id"));
        Integer bBotId = Integer.parseInt(data.getFirst("b_bot_id"));
        return startGameService.startGame(aId, aBotId, bId, bBotId);
    }
}
