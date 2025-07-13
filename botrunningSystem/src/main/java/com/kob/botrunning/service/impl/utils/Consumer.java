package com.kob.botrunning.service.impl.utils;

import com.kob.botrunning.utils.BotInterface;
import com.kob.botrunning.utils.FeignService;
import org.joor.Reflect;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.annotation.Resource;
import java.util.UUID;


@Component
public class Consumer extends Thread{
    private Bot bot;
    private static FeignService feignService;

    @Resource
    public void setFeignService(FeignService feignService) {
        Consumer.feignService = feignService;
        System.out.println("FeignService set successfully!");
    }

    // 下一步及等待时长
    public void startTimeout(long timeout, Bot bot) {
        this.bot = bot;
        this.start();
        try {
            this.join(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            this.interrupt();
        }
    }

    private String addUid(String code, String uid) {  // 在code中的Bot类名后添加uid
        int k = code.indexOf(" implements BotInterface");
        return code.substring(0, k) + uid + code.substring(k);
    }


    @Override
    public void run() {
        UUID uuid = UUID.randomUUID();
        String uid = uuid.toString().substring(0, 8);
        BotInterface botInterface = Reflect.compile(
                "com.kob.botrunning.utils.BotCode1" + uid,
                addUid(bot.getBotCode(), uid)
        ).create().get();
        Integer direction = botInterface.nextMove(bot.getInput());
        System.out.println("move-direction: " + bot.getUserId() + " " + direction);
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", bot.getUserId().toString());
        data.add("direction", direction.toString());
        feignService.sendMove(data);
    }
}
