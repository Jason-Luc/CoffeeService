package com.coffeeShop.service.support;

import com.coffeeShop.service.model.CoffeeOrder;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;

@Component
public class MessageQueue {

    public static LinkedBlockingQueue<CoffeeOrder> mockQueue;

    public MessageQueue() {

    }

    public void setQueueSize(int size) {
        mockQueue = new LinkedBlockingQueue<>(size);
    }
}
