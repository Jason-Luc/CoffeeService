package com.coffeeShop.service;

import com.coffeeShop.service.integration.Barista;
import com.coffeeShop.service.service.CoffeeOrderService;
import com.coffeeShop.service.support.MessageQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class BaristaRunner implements ApplicationRunner {

    @Autowired
    Barista barista;

    @Autowired
    MessageQueue messageQueue;

    @Autowired
    CoffeeOrderService orderService;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(100);
                    barista.makeCoffee();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        }).start();


    }


}