package com.tfs.order.mgmt.order.controller;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.tfs.order.mgmt.order.model.dto.Customer;
import com.tfs.order.mgmt.order.model.dto.Product;
import com.tfs.order.mgmt.order.model.entity.Order;
import com.tfs.order.mgmt.order.service.OrderService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class OrderController {

	@Autowired
	OrderService orderService;

	@Value(value = "${service.product-microservice.path}")
	private String productService;

	@Value(value = "${service.consumer-microservice.path}")
	private String consumerService;

	@PostMapping(value = "/createOrder")
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<ResponseEntity<?>> createOrder(@RequestBody Order order) {

		Mono<Customer> customerResponse = WebClient.create().get()
				.uri(consumerService + "/customers/{id}", order.getCustomerId()).retrieve().bodyToMono(Customer.class);

		Mono<Product> productResponse = WebClient.create().get()
				.uri(productService + "/product/getProduct/{id}", order.getProduct().getProductId()).retrieve()
				.bodyToMono(Product.class);

		return Mono.zip(customerResponse, productResponse).map(response -> {
			Customer customer = response.getT1();
			Product product = response.getT2();

			Integer productQtyInStock = product.getQtyInStock();
			if (productQtyInStock < order.getProduct().getProductCount()) {
				return new ResponseEntity<>(
						"Due to limited stock, Maximum Order limit for this product is " + productQtyInStock,
						HttpStatus.BAD_REQUEST);
			}

			double currentCreditLimit = customer.getCreditLimit();
			if (currentCreditLimit < order.getOrderAmount()) {
				return new ResponseEntity<>("Customer does not have sufficient Credit limit to place the Order.",
						HttpStatus.BAD_REQUEST);
			}

			order.setOrderId(UUID.randomUUID().toString());
			orderService.createOrder(order);
			return new ResponseEntity<>(order, HttpStatus.OK);
		});
	}

	@GetMapping(value = "/getAllOrder")
	public List<Order> getAllOrder() {
		return orderService.getAllOrder();

	}

	@GetMapping(value = "/getOrder/{id}")
	public ResponseEntity<Order> getOrderById(@PathVariable String id) {
		if (orderService.getOrderById(id).isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>(orderService.getOrderById(id).get(), HttpStatus.OK);
		}
	}

	@PutMapping(value = "/updateOrder/{id}")
	public Mono<ResponseEntity<?>> updateOrder(@PathVariable String id, @RequestBody Order order) {

		Mono<Customer> customerResponse = WebClient.create().get()
				.uri(consumerService + "/customers/{id}", order.getCustomerId()).retrieve().bodyToMono(Customer.class);

		Mono<Product> productResponse = WebClient.create().get()
				.uri(productService + "/product/getProduct/{id}", order.getProduct().getProductId()).retrieve()
				.bodyToMono(Product.class);

		return Mono.zip(customerResponse, productResponse).map(response -> {
			Customer customer = response.getT1();
			Product product = response.getT2();

			Integer productQtyInStock = product.getQtyInStock();
			if (productQtyInStock < order.getProduct().getProductCount()) {
				return new ResponseEntity<>(
						"Due to limited stock, Maximum Order limit for this product is " + productQtyInStock,
						HttpStatus.BAD_REQUEST);
			}

			double currentCreditLimit = customer.getCreditLimit();
			if (currentCreditLimit < order.getOrderAmount()) {
				return new ResponseEntity<>("Customer does not have sufficient Credit limit to place the Order.",
						HttpStatus.BAD_REQUEST);
			}

			order.setOrderId(id);
			orderService.updateOrder(id, order);
			return new ResponseEntity<>(order, HttpStatus.OK);
		});
	}

	@DeleteMapping(value = "/deleteOrder/{id}")
	public ResponseEntity<Order> deleteOrder(@PathVariable String id) {
		Order order =orderService.deleteOrder(id);
		if(Objects.isNull(order) ){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
		return new ResponseEntity<>(order, HttpStatus.OK);	
		}

	}
}
