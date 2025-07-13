package com.kob.kobbackend.controller.user.bot;

import com.kob.kobbackend.pojo.Bot;
import com.kob.kobbackend.service.bot.BotService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user/bot")
public class BotController {
    @Resource
    private BotService botService;
    @PostMapping("/add")
    public Map<String, String> addBot(@RequestParam Map<String, String> map) {
        String title = map.get("title");
        String description = map.get("description");
        String content = map.get("content");
        return botService.addBot(title, description, content);
    }

    @PostMapping ("/delete")
    public Map<String, String> deleteBot(@RequestParam Map<String, String> map) {
        return botService.deleteBot(map);
    }

    @GetMapping ("/allbot")
    public List<Bot> getAllBot() {
        return botService.getAllBot();
    }

    @PostMapping ("/update")
    public Map<String, String> updateBot(@RequestParam Map<String, String> map) {
        return botService.updateBot(map);
    }
}
