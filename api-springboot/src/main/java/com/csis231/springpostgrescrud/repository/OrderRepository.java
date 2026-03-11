package com.csis231.springpostgrescrud.repository;

import com.csis231.springpostgrescrud.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    boolean existsByOrderNumber(String orderNumber);
}