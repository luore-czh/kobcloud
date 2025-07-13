package com.kob.kobbackend.controller.ranklist;

import com.alibaba.fastjson.JSONObject;
import com.kob.kobbackend.service.ranklist.GetRankListService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
public class GetRankListController {
    @Resource
    private GetRankListService getRankListService;

    @GetMapping("/ranklist/getlist")
    public JSONObject getRankList(@RequestParam Map<String, String> data) {
        Integer page = Integer.parseInt(data.get("page"));
        return getRankListService.getRankList(page);
    }
}
