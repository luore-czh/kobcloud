package com.kob.kobbackend.service.bot;

import com.kob.kobbackend.pojo.Bot;

import java.util.List;
import java.util.Map;

public interface BotService {
    Map<String, String> addBot(String title, String description, String content);

    Map<String, String> deleteBot(Map<String, String> map);

    List<Bot> getAllBot();

    Map<String, String> updateBot(Map<String, String> map);
}
