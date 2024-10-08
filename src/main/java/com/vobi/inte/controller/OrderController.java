package com.vobi.inte.controller;

import org.springframework.messaging.Message;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vobi.inte.gateway.OrderGateway;
import com.vobi.inte.model.Order;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {
	
	OrderGateway orderGateway;

	public OrderController(OrderGateway orderGateway) {
		super();
		this.orderGateway = orderGateway;
	}
	
	
	@PostMapping("/placeOrder")
	public Order placeOrder(@RequestBody Order order) {
		Message<Order> repleyOrder=orderGateway.placeOrder(order);
		Order orderResponse=repleyOrder.getPayload();
		return orderResponse;
	}
	

}
