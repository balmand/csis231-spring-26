package com.csis231.springpostgrescrud.service;

import com.csis231.springpostgrescrud.dto.OrderDto;
import com.csis231.springpostgrescrud.entity.Order;
import com.csis231.springpostgrescrud.exeption.ResourceNotFoundException;
import com.csis231.springpostgrescrud.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    private OrderRepository orderRepository;

    @Override
    public OrderDto createOrder(OrderDto orderDto) {

        if(orderRepository.existsByOrderNumber(orderDto.getOrderNumber())){
            throw new RuntimeException("Order already exists with number: " + orderDto.getOrderNumber());
        }

        Order order = new Order();
        order.setOrderNumber(orderDto.getOrderNumber());
        order.setCustomerName(orderDto.getCustomerName());
        order.setTotalAmount(orderDto.getTotalAmount());

        Order savedOrder = orderRepository.save(order);

        return new OrderDto(
                savedOrder.getId(),
                savedOrder.getOrderNumber(),
                savedOrder.getCustomerName(),
                savedOrder.getTotalAmount()
        );
    }

    @Override
    public OrderDto getOrderById(Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        return new OrderDto(
                order.getId(),
                order.getOrderNumber(),
                order.getCustomerName(),
                order.getTotalAmount()
        );
    }

    @Override
    public List<OrderDto> getAllOrders() {

        List<Order> orders = orderRepository.findAll();

        return orders.stream()
                .map(order -> new OrderDto(
                        order.getId(),
                        order.getOrderNumber(),
                        order.getCustomerName(),
                        order.getTotalAmount()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public OrderDto updateOrder(Long id, OrderDto orderDto) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        order.setOrderNumber(orderDto.getOrderNumber());
        order.setCustomerName(orderDto.getCustomerName());
        order.setTotalAmount(orderDto.getTotalAmount());

        Order updatedOrder = orderRepository.save(order);

        return new OrderDto(
                updatedOrder.getId(),
                updatedOrder.getOrderNumber(),
                updatedOrder.getCustomerName(),
                updatedOrder.getTotalAmount()
        );
    }

    @Override
    public void deleteOrder(Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        orderRepository.delete(order);
    }
}