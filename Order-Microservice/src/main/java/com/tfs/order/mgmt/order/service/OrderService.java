package com.tfs.order.mgmt.order.service;

import java.util.List;
import java.util.Optional;

import com.tfs.order.mgmt.order.model.entity.Order;

public interface OrderService {

	public Order createOrder(Order order);

	public Optional<Order> getOrderById(String orderId);

	public List<Order> getAllOrder();

	public Order updateOrder(String orderId, Order order);

	public Order deleteOrder(String orderId);

}
