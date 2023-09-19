package com.tfs.order.mgmt.order.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import com.tfs.order.mgmt.order.enums.ActionType;
import com.tfs.order.mgmt.order.model.dto.CustomerCreditLimitEventDTO;
import com.tfs.order.mgmt.order.model.dto.ProductCountEventDTO;
import com.tfs.order.mgmt.order.model.entity.Order;
import com.tfs.order.mgmt.order.producer.ProductCustomerMsgProducer;
import com.tfs.order.mgmt.order.repository.OrderRepository;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderRepository orderRepository;


	@Autowired
	ProductCustomerMsgProducer productCustomerMsgProducer;

	@Autowired
	ProductCountEventDTO productCountEventDTO;

	@Autowired
	CustomerCreditLimitEventDTO customerCreditLimitEventDTO;

	@Autowired
	TaskExecutor taskExecutor;

	@Override
	public Order createOrder(Order order) {
		final Order[] insOrderArr = {};
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				insOrderArr[0] = orderRepository.insert(order);
				if (insOrderArr != null) {
					initiate_Kafka_Event_for_Create(insOrderArr[0]);
				}
			}
		});

		return insOrderArr[0];
	}

	@Override
	@Cacheable(value = "orders", key = "#orderId", condition = "#order!=null")
	public Optional<Order> getOrderById(String orderId) {
		return orderRepository.findById(orderId);
	}

	@Override
	public List<Order> getAllOrder() {
		return orderRepository.findAll();
	}

	@Override
	public Order updateOrder(String orderId, Order updated_order) {

		Order return_updated_order = orderRepository.save(updated_order);
		Optional<Order> order = getOrderById(orderId);

		if(order.isPresent()) {
			initiate_Kafka_Event_for_Update(order.get(), updated_order, orderId);
		}

		return return_updated_order;
	}

	@Override
	public Order deleteOrder(String orderId) {

		final Optional<Order> order = getOrderById(orderId);
		if (order.isEmpty()) {
			return null;
		}
		orderRepository.delete(order.get());
		initiate_Kafka_Event_for_Delete(order.get());
		return order.get();

	}

	/****************** Start :: intiate kafka event for create ******************/
	private void initiate_Kafka_Event_for_Create(Order order) {

		productCountEventDTO.setOrderId(order.getOrderId());
		productCountEventDTO.setProductId(order.getProduct().getProductId());
		productCountEventDTO.setProductCount(order.getProduct().getProductCount());
		productCountEventDTO.setActionType(ActionType.COUNT_DECREMENT);

		customerCreditLimitEventDTO.setOrderId(order.getOrderId());
		customerCreditLimitEventDTO.setCustomerId(order.getCustomerId());
		customerCreditLimitEventDTO.setCreditLimitAmount(order.getOrderAmount());
		customerCreditLimitEventDTO.setActionType(ActionType.CREDIT_DECREMENT);

		try {
			productCustomerMsgProducer.sendToProduct(productCountEventDTO);
			productCustomerMsgProducer.sendToCustomer(customerCreditLimitEventDTO);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	/****************** End :: intiate kafka event for create ******************/

	/******************
	 * Start :: intiate kafka event for update order
	 ******************/
	private void initiate_Kafka_Event_for_Update(Order old_order, Order updated_order, String orderId) {

		/****************** Start :: Product Count Calculation ******************/
		if (old_order.getProduct().getProductCount() != updated_order.getProduct().getProductCount()) {

			productCountEventDTO.setOrderId(orderId);
			productCountEventDTO.setProductId(updated_order.getProduct().getProductId());

			if (old_order.getProduct().getProductCount() > updated_order.getProduct().getProductCount()) {
				int count_diff = (old_order.getProduct().getProductCount())
						- (updated_order.getProduct().getProductCount());
				productCountEventDTO.setProductCount(count_diff);
				productCountEventDTO.setActionType(ActionType.COUNT_INCREMENT);

			} else {
				int count_diff = (updated_order.getProduct().getProductCount())
						- (old_order.getProduct().getProductCount());
				productCountEventDTO.setProductCount(count_diff);
				productCountEventDTO.setActionType(ActionType.COUNT_DECREMENT);
			}
			try {
				productCustomerMsgProducer.sendToProduct(productCountEventDTO);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		/****************** End :: Product Count Calculation ******************/

		/******************
		 * Start :: Customer Credit Limit Calculation
		 ******************/

		if (old_order.getOrderAmount() != updated_order.getOrderAmount()) {

			customerCreditLimitEventDTO.setOrderId(orderId);
			customerCreditLimitEventDTO.setCustomerId(updated_order.getCustomerId());

			if (old_order.getOrderAmount() > updated_order.getOrderAmount()) {
				double credit_diff = (old_order.getOrderAmount()) - (updated_order.getOrderAmount());
				customerCreditLimitEventDTO.setCreditLimitAmount(credit_diff);
				customerCreditLimitEventDTO.setActionType(ActionType.CREDIT_INCREMENT);
			} else {
				double credit_diff = (updated_order.getOrderAmount()) - (old_order.getOrderAmount());
				customerCreditLimitEventDTO.setCreditLimitAmount(credit_diff);
				customerCreditLimitEventDTO.setActionType(ActionType.CREDIT_DECREMENT);

			}
			try {

				productCustomerMsgProducer.sendToCustomer(customerCreditLimitEventDTO);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		}

		/****************** End :: Credit Limit Calculation ******************/

	}

	/******************
	 * End :: intiate kafka event for update order
	 ******************/

	/****************** Start :: intiate kafka event for delete ******************/
	private void initiate_Kafka_Event_for_Delete(Order order) {

		productCountEventDTO.setOrderId(order.getOrderId());
		productCountEventDTO.setProductId(order.getProduct().getProductId());
		productCountEventDTO.setProductCount(order.getProduct().getProductCount());
		productCountEventDTO.setActionType(ActionType.COUNT_INCREMENT);

		customerCreditLimitEventDTO.setOrderId(order.getOrderId());
		customerCreditLimitEventDTO.setCustomerId(order.getCustomerId());
		customerCreditLimitEventDTO.setCreditLimitAmount(order.getOrderAmount());
		customerCreditLimitEventDTO.setActionType(ActionType.CREDIT_INCREMENT);

		try {
			productCustomerMsgProducer.sendToProduct(productCountEventDTO);
			productCustomerMsgProducer.sendToCustomer(customerCreditLimitEventDTO);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
	/****************** End :: intiate kafka event for delete ******************/

}
