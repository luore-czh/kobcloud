package com.kob.kobbackend.service.impl.pk;

import com.kob.kobbackend.consumer.WebSocketServer;
import com.kob.kobbackend.service.pk.StartGameService;
import org.springframework.stereotype.Service;

@Service
public class StartGameServiceImpl implements StartGameService {
    @Override
    public String startGame(Integer aId,  Integer aBotId, Integer bId,  Integer bBotId) {
        System.out.println("start game: " + aId + " " +aBotId + " " + bId + " " + bBotId);
        WebSocketServer.startGame(aId, aBotId, bId, bBotId);
        return "start game success";
    }
}
