package com.coffeeShop.service.support;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class RedisMockRepository {

    public  static  ConcurrentHashMap<String, Object> mockRepository;

    public RedisMockRepository() {
        mockRepository = new ConcurrentHashMap();
    }

}
