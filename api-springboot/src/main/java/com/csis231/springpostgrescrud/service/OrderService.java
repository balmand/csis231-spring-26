package com.csis231.springpostgrescrud.service;

import com.csis231.springpostgrescrud.dto.OrderDto;

import java.util.List;

public interface OrderService {

    OrderDto createOrder(OrderDto orderDto);

    OrderDto getOrderById(Long id);

    List<OrderDto> getAllOrders();

    OrderDto updateOrder(Long id, OrderDto orderDto);

    void deleteOrder(Long id);
}