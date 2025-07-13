package com.kob.kobbackend.service.impl.record;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kob.kobbackend.mapper.RecordMapper;
import com.kob.kobbackend.mapper.UserMapper;
import com.kob.kobbackend.pojo.Record;
import com.kob.kobbackend.pojo.User;
import com.kob.kobbackend.service.record.GetRecordListService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

@Service
public class GetRecordListServiceImpl implements GetRecordListService {
    @Resource
    private RecordMapper recordMapper;
    @Resource
    private UserMapper userMapper;
    @Override
    public JSONObject getRecordList(Integer page) {
        IPage<Record> recordIPage = new Page<>(page, 10);
        QueryWrapper<Record> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("createtime");
        List<Record> records = recordMapper.selectPage(recordIPage, queryWrapper).getRecords();
        JSONObject resp = new JSONObject();

        List<JSONObject> items = new LinkedList<>();
        for(Record record : records) {
            User userA = userMapper.selectById(record.getAId());
            User userB = userMapper.selectById(record.getBId());
            JSONObject item = new JSONObject();
            item.put("a_photo", userA.getPhoto());
            item.put("a_username", userA.getUsername());
            item.put("b_photo", userB.getPhoto());
            item.put("b_username", userB.getUsername());
            String result = "平局";
            if("A".equals(record.getLoser())) result = "B胜";
            else if("B".equals(record.getLoser())) result = "A胜";
            item.put("result", result);
            item.put("record", record);
            items.add(item);
        }
        resp.put("records", items);
        resp.put("records_count", recordMapper.selectCount(null));
        return resp;
    }
}
