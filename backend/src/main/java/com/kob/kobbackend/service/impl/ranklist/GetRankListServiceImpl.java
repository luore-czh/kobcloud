package com.kob.kobbackend.service.impl.ranklist;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kob.kobbackend.mapper.UserMapper;
import com.kob.kobbackend.pojo.User;
import com.kob.kobbackend.service.ranklist.GetRankListService;
import netscape.javascript.JSObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class GetRankListServiceImpl implements GetRankListService {
    @Resource
    private UserMapper userMapper;
    @Override
    public JSONObject getRankList(Integer page) {
        IPage<User> iPage = new Page<>(page, 3);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("rating");
        List<User> users = userMapper.selectPage(iPage, queryWrapper).getRecords();
        JSONObject resp = new JSONObject();
        for(User user : users) {
            user.setPassword(""); // 清空密码，防止泄漏
        }
        resp.put("users", users);
        resp.put("users_count", userMapper.selectCount(null));
        return resp;
    }
}
