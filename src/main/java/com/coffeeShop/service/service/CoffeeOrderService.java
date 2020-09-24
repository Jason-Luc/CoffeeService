package com.coffeeShop.service.service;


import com.coffeeShop.service.model.Coffee;
import com.coffeeShop.service.model.CoffeeOrder;
import com.coffeeShop.service.model.OrderState;
import com.coffeeShop.service.repository.CoffeeOrderRepository;
import com.coffeeShop.service.support.MessageQueue;
import com.coffeeShop.service.support.OrderCounter;
import lombok.extern.slf4j.Slf4j;
import org.joda.money.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@Transactional
@Slf4j
public class CoffeeOrderService {
    String waiterName = "waiter";
    @Value("${discount}")
    Integer discount;
    @Autowired
    MessageQueue messageQueue;
    @Autowired
    private CoffeeOrderRepository orderRepository;
    private String waiterId = UUID.randomUUID().toString();
    @Autowired
    private OrderCounter orderCounter;

    public CoffeeOrder get(Long id) {
        return orderRepository.getOne(id);
    }

    public long getOrderCounter() {
        return orderCounter.getCount().longValue();
    }

    public CoffeeOrder createOrder(Long id, String customer, Coffee... coffee) {


        CoffeeOrder order = CoffeeOrder.builder()
                .id(id)
                .customer(customer)
                .items(new ArrayList<>(Arrays.asList(coffee)))
                .state(OrderState.INIT)
                .discount(discount)
                .total(calcTotal(coffee))
                .state(OrderState.INIT)
                .waiter(waiterName + "-" + waiterId)
                .barista("barista")
                .build();
        CoffeeOrder saved = orderRepository.save(order);
        orderCounter.increment();
        log.info("New Order: {} with  count = {}", saved, orderCounter.getCount());
        return saved;
    }


    private Double calcTotal(Coffee[] coffee) {


            List<Double> items = Stream.of(coffee).map(c -> c.getPrice())
                    .collect(Collectors.toList());
            Double sum = items.stream().mapToDouble(Double::doubleValue).sum();
            return sum;
    }

    public CoffeeOrder updateState(CoffeeOrder order, OrderState state) {
        if (state.compareTo(order.getState()) <= 0) {
            log.warn("Wrong State order: {}, {}", state, order.getState());
            return null;
        }
        order.setState(state);
        orderRepository.save(order);
        log.info("Updated Order: {} with id = {}, count = {}", order, order.getId(), orderCounter.getCount());
        messageQueue.mockQueue.offer(order);
        return orderRepository.getOne(order.getId());
    }


    public List<CoffeeOrder> findAllBrewedOrder() {
        return orderRepository.findByState(OrderState.DONE);
    }

    public int findAllPaidAndDoneCoffee() {
        return orderRepository.findByState(OrderState.DONE).size() + orderRepository.findByState(OrderState.PAID).size();
    }


}
