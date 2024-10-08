package com.vobi.inte.gateway;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.Message;

import com.vobi.inte.model.Order;

@MessagingGateway
public interface OrderGateway {
	
	@Gateway(requestChannel = "request-in-channel")
	public Message<Order> placeOrder(Order order);

}
