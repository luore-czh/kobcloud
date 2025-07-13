package com.kob.botrunning.utils;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@FeignClient(name = "kob-backend")
public interface FeignService {

    @PostMapping("/pk/receive/bot/move")
    String sendMove(@RequestBody MultiValueMap data);

}