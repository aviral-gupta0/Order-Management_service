package com.tfs.product.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.tfs.product.model.Product;

/**

 *
 */
public interface ProductRepository extends ReactiveMongoRepository<Product, String> {
	

}
