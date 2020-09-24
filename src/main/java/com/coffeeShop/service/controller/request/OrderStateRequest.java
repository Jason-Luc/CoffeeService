package com.coffeeShop.service.controller.request;

import com.coffeeShop.service.model.OrderState;

public class OrderStateRequest {
    private OrderState state;
    Long id;

    public OrderState getState() {
        return state;
    }

    public void setState(OrderState state) {
        this.state = state;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
