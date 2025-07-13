package com.kob.kobbackend.consumer.utils;

import com.alibaba.fastjson.JSONObject;
import com.ctc.wstx.util.WordResolver;
import com.kob.kobbackend.consumer.WebSocketServer;
import com.kob.kobbackend.pojo.Bot;
import com.kob.kobbackend.pojo.Record;
import com.kob.kobbackend.pojo.User;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;


public class Game extends Thread {
    final private Integer rows;
    final private Integer cols;
    final private Integer inner_walls_count; //地图内部障碍物数量
    final private int[][] g;
    final private static int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
    private final Player playerA, playerB; // 两名玩家
    private Integer nextStepA = null;
    private Integer nextStepB = null;
    private ReentrantLock lock = new ReentrantLock();
    //对局状态
    private String status = "playing";  // playing -> finished
    // 对局结果
    private String loser = "";  // all: 平局，A: A输，B: B输

    public Game(Integer rows, Integer cols, Integer inner_walls_count, Integer idA, Bot botA, Integer idB, Bot botB) {
        this.rows = rows;
        this.cols = cols;
        this.inner_walls_count = inner_walls_count;
        this.g  = new int[rows][cols];
        Integer botIdA = -1, botIdB = -1;
        String botCodeA = "", botCodeB = "";
        if (botA != null) {
            botIdA = botA.getId();
            botCodeA = botA.getContent();
        }
        if (botB != null) {
            botIdB = botB.getId();
            botCodeB = botB.getContent();
        }
        playerA = new Player(idA, botIdA, botCodeA, rows - 2, 1, new ArrayList<>());
        playerB = new Player(idB, botIdB, botCodeB, 1, cols - 2, new ArrayList<>());

    }

    public Player getPlayerA() {
        return playerA;
    }

    public Player getPlayerB() {
        return playerB;
    }

    public int[][] getG() {
        return g;
    }

    public void setNextStepA(Integer nextStepA) {
        lock.lock();
        try {
            this.nextStepA = nextStepA;
        } finally {
            lock.unlock();
        }
    }

    public void setNextStepB(Integer nextStepB) {
        lock.lock();
        try {
            this.nextStepB = nextStepB;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 向两个客户端传递移动信息
     */
    private void sendMove() {
        lock.lock();
        try {
            JSONObject resp = new JSONObject();
            resp.put("event", "move");
            resp.put("a_direction", nextStepA);
            resp.put("b_direction", nextStepB);
            sendAllMessage(resp.toJSONString());
            nextStepA = nextStepB = null;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 传递对局状态给Bot系统
     * @param player
     */
    private void sendBotMessage(Player player) {
        if(player.getBotId().equals(-1)) {
            return; // 不需要机器人
        }
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", player.getId().toString());
        data.add("bot_code", player.getBotCode());
        data.add("input", getInput(player));
        WebSocketServer.feignService2.addBot(data); //发送给Bot端
    }

    /**
     * 需要传递给Bot系统的消息集合
     * @param player
     * @return
     */
    private String getInput(Player player) {
        Player me, you;
        if(playerA.getId().equals(player.getId())) {
            me = playerA;
            you = playerB;
        } else {
            me = playerB;
            you = playerA;
        }
        return getMapString() + "#" +
                me.getSx() + "#" +
                me.getSy() + "#(" +
                me.getStepString() + ")#" +
                you.getSx() + "#" +
                you.getSy() + "#(" +
                you.getStepString() + ")";
    }
    private void sendAllMessage(String message) {
        if (WebSocketServer.users.get(playerA.getId()) != null)
            WebSocketServer.users.get(playerA.getId()).sendMessage(message);
        if (WebSocketServer.users.get(playerB.getId()) != null)
            WebSocketServer.users.get(playerB.getId()).sendMessage(message);
    }


    /**
     * 先前端发送结果
     */
    private void sendResult() {
        JSONObject resp = new JSONObject();
        resp.put("event", "result");
        resp.put("loser", loser);
        saveToDatabase();
        sendAllMessage((resp.toJSONString()));
    }

    /**
     * 判断两名玩家下一步操作是否合法
     */
    private void judge() {
        List<Cell> cellsA = playerA.getCells();
        List<Cell> cellsB = playerB.getCells();

        boolean validA = checkValid(cellsA, cellsB);
        boolean validB = checkValid(cellsB, cellsA);
        if(!validA || !validB) {
            status = "finished";
            if(!validA && !validB) loser = "all";
            else if(!validA) loser = "A";
            else loser = "B";
        }
    }

    private boolean checkValid(List<Cell> cellsA, List<Cell> cellsB) {
        int n = cellsA.size();
        Cell cell = cellsA.get(n-1);
        if(g[cell.x][cell.y] == 1) return false;
        for (int i = 0; i < n - 1; i++) {
            if(cellsA.get(i).x == cell.x && cellsA.get(i).y == cell.y) return false;
        }
        for (int i = 0; i < n - 1; i++) {
                if(cellsB.get(i).x == cell.x && cellsB.get(i).y == cell.y) return false;
        }
        return true;
    }

    /**
     * 测试两个玩家的对应起点的连通性
     * @param sx
     * @param sy
     * @param tx
     * @param ty
     * @return
     */
    private boolean check_connectivity(int sx, int sy, int tx, int ty) {
        if(sx == tx && sy == ty) return true;
        g[sx][sy] = 1;
        for (int i = 0; i < 4; i++) {
            int x = sx + dx[i], y = sy + dy[i];
            if(x >= 0 && x < this.rows && y >= 0 && y < this.cols && g[x][y] == 0) {
                if(check_connectivity(x, y, tx, ty)) {
                    g[sx][sy] = 0;
                    return true;
                }
            }
        }
        g[sx][sy] = 0;
        return false;
    }

    private boolean draw() {
        for(int i = 0; i < this.rows; i++) {
            for(int j = 0; j < this.cols; j++) {
                g[i][j] = 0;
            }
        }
        // 四周加上障碍物
        for (int r = 0; r < this.rows; r++) {
            g[r][0] = g[r][this.cols-1] = 1;
        }
        for (int c = 0; c < this.cols; c++) {
            g[0][c] = g[this.rows - 1][c] = 1;
        }
        Random random = new Random();
        for (int i = 0; i < this.inner_walls_count / 2; i++) {
            for(int j = 0; j < 1000; j++) {
                int r = random.nextInt(this.rows);
                int c = random.nextInt(this.cols);
                if(g[r][c] == 1 || g[this.rows - 1 - r][this.cols - 1 - c] == 1) continue; // 已经有障碍物则跳过
                if(r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2) continue; //起点不能有障碍物
                g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = 1;
                break;
            }
        }
        return check_connectivity(this.rows - 2, 1, 1, this.cols - 2);
    }

    public void createMap() {
        for (int i = 0; i < 1000; i ++ ) {
            if (draw())
                break;
        }
    }

    /**
     * 更新游戏积分
     * @param player
     * @param rating
     */
    private void updateUserRating(Player player, Integer rating) {
        User user = WebSocketServer.userMapper.selectById(player.getId());
        user.setRating(rating);
        WebSocketServer.userMapper.updateById(user);
    }

    /**
     * 保存对局信息到数据库
     */
    private void saveToDatabase() {
        Integer ratingA = WebSocketServer.userMapper.selectById(playerA.getId()).getRating();
        Integer ratingB = WebSocketServer.userMapper.selectById(playerB.getId()).getRating();
        if("A".equals(loser)) {
            ratingA -= 2;
            ratingB += 5;
        } else if ("B".equals(loser)) {
            ratingA += 5;
            ratingB -= 2;
        }
        updateUserRating(playerA, ratingA);
        updateUserRating(playerB, ratingB);
        Record record = new Record(
                null,
                playerA.getId(),
                playerA.getSx(),
                playerA.getSy(),
                playerB.getId(),
                playerB.getSx(),
                playerB.getSy(),
                playerA.getStepString(),
                playerB.getStepString(),
                getMapString(),
                loser,
                new Date()
        );
        WebSocketServer.recordMapper.insert(record);
    }

    private String getMapString() {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                res.append(g[i][j]);
            }
        }
        return res.toString();
    }


    /**
     * 是否成功获取两个玩家的下一步操作
     * @return
     */
     private boolean nextStep() {
         try {
             Thread.sleep(1000);
         } catch (InterruptedException e) {
             throw new RuntimeException(e);
         }
         sendBotMessage(playerA);
         sendBotMessage(playerB);
         for(int i = 0; i < 50; i++) {
             try {
                 Thread.sleep(100);
                 lock.lock();
                 try {
                     if(nextStepA != null && nextStepB != null) {
                         playerA.getSteps().add(nextStepA);
                         playerB.getSteps().add(nextStepB);
                         return true;
                     }
                 } finally {
                     lock.unlock();
                 }
             } catch (InterruptedException e) {
                 throw new RuntimeException(e);
             }
         }
         return false;
     }

    @Override
    public void run() {
        for (int i = 0; i < 1000; i++) {
            if(nextStep()) {
                judge(); //判断对局状态
                if(status.equals("playing")) {
                    sendMove(); //移动
                } else {
                    sendResult(); //结束
                    break;
                }
            } else {
                status = "finished";
                lock.lock();
                try {
                    if(nextStepA == null && nextStepB == null) {
                        loser = "all";
                    } else if (nextStepA == null) {
                        loser = "A";
                    } else {
                        loser = "B";
                    }
                } finally {
                    lock.unlock();
                }
                sendResult();
                break;
            }
        }
    }
}
