package com.voi.study.canal.cache_control.task;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author : liu·yu
 * date : 2/23/22
 */
@Component
@AllArgsConstructor
public class Task {

    private RedisTemplate<String,String> redisTemplate;

    @PostConstruct
    public void init(){
        new Thread(new SimpleCanalClientTask(redisTemplate)).start();
    }

}
