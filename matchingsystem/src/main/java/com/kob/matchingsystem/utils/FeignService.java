package com.kob.matchingsystem.utils;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@FeignClient(name = "kob-backend")
public interface FeignService {

    @PostMapping("/pk/start/game")
    String startGame(@RequestBody MultiValueMap data);

}