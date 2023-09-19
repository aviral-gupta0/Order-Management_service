package com.tfs.order.mgmt.customer.controller;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.tfs.order.mgmt.customer.domain.entity.Customer;
import com.tfs.order.mgmt.customer.service.CustomerService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * This Controller class is responsible to expose following APIs of Customer entity :
 * <ol>
 * 		<li>API to retrieve the list of all available Customers.</li>
 * 		<li>API to retrieve a customer details by Customer Id.</li>
 * 		<li>API to responsible to create a new Customer.</li>
 * 		<li>API to responsible to update the details of an existing Customer.</li>
 * 		<li>API to responsible to delete the details of an existing Customer.</li>
 * </ol> 
 *
 *
 */
@RestController
@RequestMapping("/customers")
public class CustomerController {

	private static final Logger LOG = LoggerFactory.getLogger(CustomerController.class);
	
	@Autowired
	CustomerService customerService;
	
	/**
	 * This API is responsible to retrieve the list of all available Customers.
	 * 
	 *
	 * 
	 * @return Flux<Customer>
	 */
	@GetMapping(value = "/")
	public Flux<Customer> getAllCustomer(){
		
		LOG.debug("Get all Customers call initiated at : [" + new Date() + "]");
		return customerService.findAllCustomer();
    }
	
	/**
	 * This API is responsible to retrieve a customer details by Customer Id.
	 * 
	 * @param id
	 * 			Customer Id
	 * 
	 *
	 * 
	 * @return Mono<ResponseEntity<Customer>>
	 */
	@GetMapping(value = "/{id}")
    public Mono<ResponseEntity<Customer>> getCustomerById(@PathVariable String id){
		
		LOG.debug("Get Customer by Customer Id[" + id + "] call initiated at : [" + new Date() + "]");
		
        return customerService.findCustomerById(id)
                .map((customer) -> new ResponseEntity<>(customer, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
	
	/**
	 * This API is responsible to create a new Customer
	 * 
	 * @param customer
	 * 			Customer Entity
	 * 
	 *
	 * 
	 * @return Mono<Customer>
	 */
	@PostMapping(value = "/")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Customer> createItem(@RequestBody Customer customer){
		
		LOG.debug("Create Customer call initiated at [" + new Date() + "] with request payload: " 
						+ customer.toString());
		
        return customerService.saveCustomer(customer);
    }
	
	/**
	 * This API is responsible to update the details of an existing Customer
	 * 
	 * @param id
	 * 			Customer Id
	 * @param customer
	 * 			Customer Entity
	 * 
	 * @return Mono<ResponseEntity<Customer>>
	 */
	@PutMapping(value = "/{id}")
    public Mono<ResponseEntity<Customer>> updateItem(@PathVariable String id,
                                                 @RequestBody Customer customer){
		
		LOG.debug("Update Customer[Customer Id: " + id + "] call initiated at [" + new Date() + "] with request payload: " 
				+ customer.toString());
		
        return customerService.updateCustomer(id, customer)
                .map((customerRes) -> new ResponseEntity<>(customerRes, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

	/**
	 * This API is responsible to delete an existing Customer
	 * 
	 * @param id
	 * 			Customer Id
	 * 
	 * @return Mono<ResponseEntity<Customer>>
	 */
	@DeleteMapping(value = "/{id}")
    public Mono<ResponseEntity<Customer>> deleteItem(@PathVariable String id){
		
		LOG.debug("Delete Customer by Customer Id[" + id + "] call initiated at : [" + new Date() + "]");
		
        return customerService.deleteCustomer(id)
                .map((customer) -> new ResponseEntity<>(customer, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}