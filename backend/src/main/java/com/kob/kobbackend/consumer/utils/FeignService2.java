package com.kob.kobbackend.consumer.utils;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@FeignClient(name = "botrunning")
public interface FeignService2 {

    @PostMapping("/bot/add")
    String addBot(@RequestBody MultiValueMap data);

}