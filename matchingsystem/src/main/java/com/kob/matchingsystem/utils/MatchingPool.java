package com.kob.matchingsystem.utils;

import com.kob.matchingsystem.pojo.Player;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class MatchingPool extends Thread{
    private static List<Player> players = new ArrayList<>();
    private final ReentrantLock lock = new ReentrantLock();
    private static FeignService feignService;

    @Resource
    public void setFeignService(FeignService feignService) {
        MatchingPool.feignService = feignService;
    }
    /**
     * 添加匹配用户
     * @param userId
     * @param rating
     */
    public void addPlayer(Integer userId, Integer rating, Integer botId) {
        lock.lock();
        try {
            players.add(new Player(userId, rating, botId, 0));
        } finally {
            lock.unlock();
        }
    }

    public void removePlayer(Integer userId) {
        lock.lock();
        try {
            List<Player> newPlayers = new ArrayList<>();
            for(Player player : players) {
                if(!player.getUserId().equals(userId)) newPlayers.add(player);
            }
            players = newPlayers;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 定义匹配规则, 分差小于两名玩家共同等待时间 * 10
     * @param a
     * @param b
     * @return
     */
    private boolean checkMatched(Player a, Player b) {
        int ratingDelta = Math.abs(a.getRating() - b.getRating());
        int waitingTime = Math.min(a.getWaitingTime(), b.getWaitingTime());
        return ratingDelta <= waitingTime * 10;
    }

    /**
     * 每一秒所有玩家等待时间+1
     */
    private void increaseWaitingTime() {
        for(Player player:players) {
            player.setWaitingTime(player.getWaitingTime() + 1);
        }
    }

    private void matchPlayers() { //匹配所有玩家
        // System.out.println("matching players: " + players.toString());
        boolean[] used = new boolean[players.size()];
        for (int i = 0; i < players.size(); i++) {
            //如果该玩家已匹配，则跳过
            if(used[i]) continue;
            for (int j = i+1; j < players.size(); j++) {
                if(used[j]) continue;
                // 获取两名玩家
                Player a = players.get(i), b = players.get(j);
                if(checkMatched(a, b)) {
                    used[i] = used[j] = true;
                    // 返回匹配结果
                    sendResult(a, b);
                    break;
                }
            }
        }
        List<Player> newPlayers = new ArrayList<>();
        for (int i = 0; i < players.size(); i ++ ) {
            if (!used[i]) {
                newPlayers.add(players.get(i));
            }
        }
        players = newPlayers;

    }

    /**
     * 返回匹配结果
     * @param a
     * @param b
     */
    private void sendResult(Player a, Player b) {

        System.out.println("send Result: " + a + " " + b);
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("a_id", a.getUserId().toString());
        data.add("a_bot_id", a.getBotId().toString());
        data.add("b_id", b.getUserId().toString());
        data.add("b_bot_id", b.getBotId().toString());
        // 发送给backend
        feignService.startGame(data);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
                lock.lock();
                try {
                    increaseWaitingTime();
                    matchPlayers();
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
