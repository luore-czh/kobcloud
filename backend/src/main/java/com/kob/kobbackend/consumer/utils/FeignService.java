package com.kob.kobbackend.consumer.utils;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@FeignClient(name = "kob-matchingsystem")
public interface FeignService {

    @PostMapping("/player/add")
    String addPlayer(@RequestBody MultiValueMap data);

    @PostMapping("/player/remove")
    String removePlayer(@RequestBody MultiValueMap data);
}