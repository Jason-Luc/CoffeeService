package com.coffeeShop.service.support;

import org.springframework.stereotype.Component;

@Component
public class OrderCounter {

    private String counterName;
    private Long counter;

    public OrderCounter() {
        this.counterName = "for coffee order counter";
        counter= new Long(0);
    }

    public String getCounterName() {
        return counterName;
    }

    public void setCounterName(String name) {
        this.counterName = counterName;
    }

    public synchronized void increment() {
        counter++;
    }


    public Long getCount() {
        return counter;
    }

}
