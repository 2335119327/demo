package com.example.demo;

import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author dong
 * @date 2022/6/14 22:26
 */
@SpringBootTest
public class redissonTest {

    @Resource
    private RedissonClient redissonClient;

    @Test
    public void test() {

        RLock lock = redissonClient.getLock("aa");

        lock.lock();

    }

}
