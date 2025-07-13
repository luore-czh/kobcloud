package com.kob.kobbackend.service.ranklist;


import com.alibaba.fastjson.JSONObject;

public interface GetRankListService {
    JSONObject getRankList(Integer page);
}
