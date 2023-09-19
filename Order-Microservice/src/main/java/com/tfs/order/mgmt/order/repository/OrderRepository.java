package com.tfs.order.mgmt.order.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.tfs.order.mgmt.order.model.entity.Order;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {

}
