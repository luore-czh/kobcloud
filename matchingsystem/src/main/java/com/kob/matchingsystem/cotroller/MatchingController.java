package com.kob.matchingsystem.cotroller;

import com.kob.matchingsystem.service.MatchingService;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class MatchingController {
    @Resource
    private MatchingService matchingService;

    @PostMapping("/player/add")
    public String addPlayer(@RequestParam MultiValueMap<String, String> data) {
        Integer userId = Integer.parseInt(data.getFirst("user_id"));
        Integer rating = Integer.parseInt(data.getFirst("rating"));
        int botId = Integer.parseInt(data.getFirst("bot_id"));
        return matchingService.addPlayer(userId, rating, botId);
    }

    @PostMapping("/player/remove")
    public String removePlayer(@RequestParam MultiValueMap<String, String> data) {
        Integer userId = Integer.parseInt(data.getFirst("user_id"));
        return matchingService.removePlayer(userId);
    }

}
