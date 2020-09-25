package com.coffeeShop.service.controller;

import com.coffeeShop.service.integration.Barista;
import com.coffeeShop.service.service.CoffeeOrderService;
import com.coffeeShop.service.support.MessageQueue;
import com.coffeeShop.service.support.RedisMockRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.coffeeShop.service.support.ConstValue.*;

@RestController
@RequestMapping("/statics")
@Slf4j
public class StaticsController {

    @Autowired
    MessageQueue messageQueue;
    @Autowired
    private RedisMockRepository redisRepository;
    @Autowired
    private CoffeeOrderService orderService;
    @Autowired
    private Barista barista;

    @RequestMapping(value = "/countAllBrewedCoffee", method = RequestMethod.GET)
    public List<Long> ccountAllBrewedCoffee() {
        return Arrays.asList(barista.getAllDoneCoffee());
    }

    @RequestMapping(value = "/countAllOrderedCoffee", method = RequestMethod.GET)
    public List<Long> countAllOrderedCoffee() {
        Long orderCounter = orderService.getOrderCounter();
        return Arrays.asList(orderCounter);
    }


    @RequestMapping(value = "/checkCustomerWaitingTime", method = RequestMethod.GET)
    public List<Boolean> checkCustomerWaitingTime() {
        finishAllOrders();
        Long maxCoffeeTime = calcMaxCoffeeProcessingTime();
        Long totalHTTPRequestLatency = calcTotalHTTPRequestLatency();
        Boolean result = (maxCoffeeTime < FIVE_SECONDS);
        if (!result) {
            log.error("checkCustomerWaitingTime failed because maxCoffeeTime {} >= 5000 ms", maxCoffeeTime);
        }
        return Arrays.asList(result);
    }


    @RequestMapping(value = "/checkHTTPProcessLatency", method = RequestMethod.GET)
    public List<Boolean> checkHTTPProcessLatency() {
        Long totalHTTPRequestLatency = calcTotalHTTPRequestLatency();
        Boolean result = (totalHTTPRequestLatency < TWO_SECONDS);
        if (!result) {
            log.info("checkHTTPProcessLatency failed because totalHTTPRequestLatency {} >=2 seconds");
        }
        return Arrays.asList(result);
    }

    @RequestMapping(value = "/comparedOrderedAndBrewed", method = RequestMethod.GET)
    public List<Boolean> comparedOrderedAndBrewed() {
        finishAllOrders();
        long allBrewedCoffee = barista.getAllDoneCoffee();
        long allOrderedCoffee = orderService.getOrderCounter();
        Boolean result = (allBrewedCoffee == allOrderedCoffee);
        if (!result) {
            log.info("comparedOrderedAndBrewed failed because allBrewedCoffee {} != allOrderedCoffee {}", allBrewedCoffee, allOrderedCoffee);
        }
        return Arrays.asList(result);
    }


    private List<Boolean> finishAllOrders() {
        while (barista.getAllDoneCoffee() < orderService.findAllPaidAndDoneCoffee() || messageQueue.mockQueue.size() != 0) {
            try {
                Thread.sleep(ONE_SECOND);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return Arrays.asList(Boolean.TRUE);
    }


    private Long calcTotalHTTPRequestLatency() {
        return getTotalTime(EACH_REQUEST_TIME_COST_REDIS_KEY);
    }

    private Long calcMaxCoffeeProcessingTime() {
        return getMaxTime(EACH_COFFEE_TIME_COST_REDIS_KEY);
    }


    private Long getTotalTime(String redisKey) {

        Long totalTime = Long.valueOf(-1);
        List<Map<String, Long>> eachRequestTimeCostList = (List) (redisRepository.mockRepository.get(redisKey));

        log.info("for each HTTP request latency（ms）： ");
        for (Map<String, Long> eachMap : eachRequestTimeCostList) {
            for (Map.Entry<String, Long> entry : eachMap.entrySet()) {
                log.info("       {} :  {}", entry.getKey(), entry.getValue());
                totalTime += entry.getValue();
            }
        }
        log.info("total HTTP request process latency = {}\n", totalTime);
        return totalTime;
    }


    private Long getMaxTime(String redisKey) {
        Long maxTime = Long.valueOf(-1);
        List<Map<String, Long>> eachRequestTimeCostList = (List) (redisRepository.mockRepository.get(redisKey));

        log.info("for each coffee making, latency of each:");
        for (Map<String, Long> eachMap : eachRequestTimeCostList) {
            for (Map.Entry<String, Long> entry : eachMap.entrySet()) {
                log.info("       {} :  {}", entry.getKey(), entry.getValue());
                if (entry.getValue() > maxTime) {
                    maxTime = entry.getValue();
                }
            }
        }
        log.info("max latency of coffee = {}\n", maxTime);
        return maxTime;
    }
}
