package com.coffeeShop.service.controller;

import com.coffeeShop.service.controller.request.NewOrderRequest;
import com.coffeeShop.service.controller.request.OrderStateRequest;
import com.coffeeShop.service.model.Coffee;
import com.coffeeShop.service.model.CoffeeOrder;
import com.coffeeShop.service.service.CoffeeOrderService;
import com.coffeeShop.service.service.CoffeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/order")
@Slf4j
public class CoffeeOrderController {
    @Autowired
    private CoffeeOrderService orderService;
    @Autowired
    private CoffeeService coffeeService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public List<CoffeeOrder> getOrder(@PathVariable("id") Long id) {
        CoffeeOrder order = orderService.get(id);
        if (order.getItems().size() == 0) {
            return Collections.emptyList();
        }
        return Arrays.asList(order);
    }

    @RequestMapping(value = "/create/{id}", method = RequestMethod.POST, consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public CoffeeOrder create(@PathVariable("id") Long id,@RequestBody NewOrderRequest newOrder) {
        log.info("Receive new Order {}", newOrder);
        Coffee[] coffeeList = coffeeService.getCoffeeByName(newOrder.getItems())
                .toArray(new Coffee[]{});
        return orderService.createOrder(id,newOrder.getCustomer(), coffeeList);
    }

    @RequestMapping(value = "/pay", method = RequestMethod.PUT, consumes = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CoffeeOrder payOrder(@RequestBody OrderStateRequest orderStateRequest) {
        CoffeeOrder coffeeOrder = orderService.get(orderStateRequest.getId());
        log.info("ready to update the  order {} status to {}", coffeeOrder.getId(), orderStateRequest);
        return orderService.updateState(coffeeOrder, orderStateRequest.getState());
    }
}
