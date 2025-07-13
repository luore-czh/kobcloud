package com.kob.kobbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kob.kobbackend.pojo.Record;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RecordMapper extends BaseMapper<Record> {
}
