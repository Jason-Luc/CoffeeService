package com.coffeeShop.service.support;

import com.coffeeShop.service.integration.Barista;
import com.coffeeShop.service.model.CoffeeOrder;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

@Component
public class MessageQueue {

    public static Queue<CoffeeOrder> mockQueue;

    public MessageQueue(Barista barista) {
        mockQueue = new LinkedBlockingDeque<>(barista.baristaCount*10);
    }
    public void setQueueSize(int size){

    }
}
