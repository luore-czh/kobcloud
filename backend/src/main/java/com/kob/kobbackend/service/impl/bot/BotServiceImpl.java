package com.kob.kobbackend.service.impl.bot;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.kobbackend.mapper.BotMapper;
import com.kob.kobbackend.pojo.Bot;
import com.kob.kobbackend.pojo.User;
import com.kob.kobbackend.service.bot.BotService;
import com.kob.kobbackend.utils.UserUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BotServiceImpl implements BotService {
    @Resource
    private BotMapper botMapper;
    @Override
    public Map<String, String> addBot(String title, String description, String content) {
        User user = UserUtil.getUser();
        Integer id = user.getId();
        Map<String, String> map = new HashMap<>();
        if(title.length() > 100) {
            map.put("show_message", "标题长度不能大于100");
            return map;
        }
        if (title == null || title.length() == 0) {
            map.put("show_message", "标题不能为空");
            return map;
        }
        if (description == null || description.length() == 0) {
            description = "这个用户很懒，什么也没留下~";
        }

        if (description.length() > 300) {
            map.put("show_message", "Bot描述的长度不能大于300");
            return map;
        }

        if (content == null || content.length() == 0) {
            map.put("show_message", "代码不能为空");
            return map;
        }

        if (content.length() > 10000) {
            map.put("show_message", "代码长度不能超过10000");
            return map;
        }
        QueryWrapper<Bot> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user.getId());
        if(botMapper.selectCount(queryWrapper) >= 10) {
            map.put("show_message", "每个用户最多创建10个Bot");
            return map;
        }
        Date date = new Date();
        System.out.println(date);
        Bot bot = new Bot(null, id, title, description, content, date, date);
        botMapper.insert(bot);
        map.put("show_message", "success");
        return map;
    }

    @Override
    public Map<String, String> deleteBot(Map<String, String> map) {
        User user = UserUtil.getUser();
        int userId = user.getId();
        int botId = Integer.parseInt(map.get("bot_id"));
        Bot bot = botMapper.selectById(botId);

        if(bot == null) {
            map.put("show_message", "bot不存在或已被删除");
            return map;
        }
        if(!bot.getUserId().equals(userId)) {
            map.put("show_message", "没有权限删除该Bot");
        }
        botMapper.deleteById(botId);
        map.put("show_message", "success");
        return map;
    }

    @Override
    public List<Bot> getAllBot() {
        User user = UserUtil.getUser();
        int userId = user.getId();
        QueryWrapper<Bot> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return botMapper.selectList(queryWrapper);
    }

    @Override
    public Map<String, String> updateBot(Map<String, String> map) {
        User user = UserUtil.getUser();
        Integer userId = user.getId();
        int bot_id = Integer.parseInt(map.get("bot_id"));

        String title =map.get("title");
        String description = map.get("description");
        String content = map.get("content");

        Map<String, String> hm = new HashMap<>();

        if (title == null || title.length() == 0) {
            hm.put("show_message", "标题不能为空");
            return hm;
        }

        if (title.length() > 100) {
            hm.put("show_message", "标题长度不能大于100");
            return hm;
        }

        if (description == null || description.length() == 0) {
            description = "这个用户很懒，什么也没留下~";
        }

        if (description.length() > 300) {
            hm.put("show_message", "Bot描述的长度不能大于300");
            return hm;
        }

        if (content == null || content.length() == 0) {
            hm.put("show_message", "代码不能为空");
            return hm;
        }

        if (content.length() > 10000) {
            hm.put("show_message", "代码长度不能超过10000");
            return hm;
        }

        Bot bot = botMapper.selectById(bot_id);

        if (bot == null) {
            hm.put("show_message", "Bot不存在或已被删除");
            return hm;
        }

        if (!bot.getUserId().equals(user.getId())) {
            hm.put("show_message", "没有权限修改该Bot");
            return hm;
        }
        Bot bot1 = new Bot(bot_id, userId, title, description, content, bot.getCreatetime(), new Date());
        botMapper.updateById(bot1);
        hm.put("show_message", "success");
        return hm;
    }
}
