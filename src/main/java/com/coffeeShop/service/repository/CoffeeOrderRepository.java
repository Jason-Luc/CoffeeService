package com.coffeeShop.service.repository;

import com.coffeeShop.service.model.CoffeeOrder;
import com.coffeeShop.service.model.OrderState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoffeeOrderRepository extends JpaRepository<CoffeeOrder, Long> {
    List<CoffeeOrder> findByState(OrderState state);
}
