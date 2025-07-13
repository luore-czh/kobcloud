package com.kob.kobbackend.consumer.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Cell {  // 标记蛇身体的每一个单元
    int x, y;
}
