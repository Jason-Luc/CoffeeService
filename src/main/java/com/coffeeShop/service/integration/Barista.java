package com.coffeeShop.service.integration;

import com.coffeeShop.service.model.CoffeeOrder;
import com.coffeeShop.service.model.OrderState;
import com.coffeeShop.service.repository.CoffeeOrderRepository;
import com.coffeeShop.service.support.MessageQueue;
import com.coffeeShop.service.support.RedisMockRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.coffeeShop.service.support.ConstValue.EACH_COFFEE_TIME_COST_REDIS_KEY;
import static com.coffeeShop.service.support.ConstValue.THREE_SECONDS;

@Slf4j
@Component
public class Barista {

    @Autowired
    MessageQueue messageQueue;
    @Autowired
    RedisMockRepository redisRepository;
    @Autowired
    private CoffeeOrderRepository orderRepository;
    public static final int baristaCount =1;
    private static long allBrewedCoffee = 0;

    public Barista(MessageQueue messageQueue) {
        this.messageQueue = messageQueue;
    }

    public void makeCoffee() {

        CoffeeOrder order = messageQueue.mockQueue.poll();
        if (order != null) {
            try {
                //it takes 3 second to make a coffee
                Thread.sleep(THREE_SECONDS);
                log.info("Coffee #{} ready, {} pending", order.getId(), messageQueue.mockQueue.size());
                order.setState(OrderState.DONE);
                orderRepository.save(order);
                CoffeeOrder coffeeOrder = orderRepository.getOne(order.getId());
                updateEachCoffeeTimeCost(coffeeOrder);
                allBrewedCoffee++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public long getAllDoneCoffee() {
        return allBrewedCoffee;
    }

    private void updateEachCoffeeTimeCost(CoffeeOrder coffeeOrder) {
        List<Map<String, Long>> existingEachCoffeeTimeCostList = (List) (redisRepository.mockRepository.get(EACH_COFFEE_TIME_COST_REDIS_KEY));
        if (existingEachCoffeeTimeCostList == null) {
            existingEachCoffeeTimeCostList = new ArrayList<>();
        }
        Map eachCoffeeTimeCostMap = new HashMap();
        eachCoffeeTimeCostMap.put(coffeeOrder.getId(), coffeeOrder.getUpdateTime().getTime() - coffeeOrder.getCreateTime().getTime());
        existingEachCoffeeTimeCostList.add(eachCoffeeTimeCostMap);
        redisRepository.mockRepository.put(EACH_COFFEE_TIME_COST_REDIS_KEY, existingEachCoffeeTimeCostList);
    }


}
