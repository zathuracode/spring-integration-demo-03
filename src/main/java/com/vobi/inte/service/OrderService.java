package com.vobi.inte.service;

import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.amqp.outbound.AmqpOutboundEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Transformers;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

import com.vobi.inte.model.Order;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@Service
@EnableIntegration
public class OrderService {
	
	

	// Definir las colas de RabbitMQ
	@Bean
	Queue orderQueue() {
		return new Queue("orderQueue", false);
	}

	@Bean
	Queue replyQueue() {
		return new Queue("replyQueue", false);
	}

	// Canal para procesar órdenes
	@Bean(name = "order-process-channel")
	MessageChannel orderProcessChannel() {
		return new DirectChannel(); // Usamos DirectChannel para comunicación directa con RabbitMQ
	}

	// Canal para respuestas
	@Bean(name = "order-repley-channel")
	MessageChannel orderReplyChannel() {
		return new DirectChannel(); // DirectChannel para enviar las respuestas a RabbitMQ
	}

	// Enviar órdenes a RabbitMQ
	@Bean
	@ServiceActivator(inputChannel = "order-process-channel")
	AmqpOutboundEndpoint amqpOutboundOrder(RabbitTemplate rabbitTemplate) {
		AmqpOutboundEndpoint outbound = new AmqpOutboundEndpoint(rabbitTemplate);
		outbound.setRoutingKey("orderQueue"); // Enviar a la cola orderQueue
		return outbound;
	}

	// Enviar respuestas a RabbitMQ
	@Bean
	@ServiceActivator(inputChannel = "order-repley-channel")
	AmqpOutboundEndpoint amqpOutboundReply(RabbitTemplate rabbitTemplate) {
		AmqpOutboundEndpoint outbound = new AmqpOutboundEndpoint(rabbitTemplate);
		outbound.setRoutingKey("replyQueue"); // Enviar respuestas a la cola replyQueue
		return outbound;
	}

	@Bean
	IntegrationFlow amqpInboundOrderFlow(ConnectionFactory connectionFactory) {
		return IntegrationFlow.from(Amqp.inboundAdapter(connectionFactory, "orderQueue"))
				.transform(Transformers.fromJson(Order.class))
				.handle((payload, headers) -> {
					Order order=(Order)payload;
					log.info("Procesando la orden de orderQueue: " + order);
					order.setOrderStatus("OK");
					return order;
				}).channel("order-repley-channel") // Enviar respuesta al canal de respuesta
				.get();
	}

	// Flujo para recibir y manejar respuestas desde RabbitMQ
	@Bean
	IntegrationFlow amqpInboundReplyFlow(ConnectionFactory connectionFactory) {
		return IntegrationFlow.from(Amqp.inboundAdapter(connectionFactory, "replyQueue"))
				.transform(Transformers.fromJson(Order.class))
				.handle((payload, headers) -> {
					Order order=(Order)payload;
					log.info("Recibida la respuesta: " + order);
					return null;
				})
				.get();
	}

	@ServiceActivator(inputChannel = "request-in-channel", outputChannel = "order-process-channel")
	public Message<Order> placeOrder(Message<Order> messageOrder) {
		log.info("Se llamo placeOrder");
		return messageOrder;
	}
	
	/*
	@ServiceActivator(inputChannel = "order-repley-channel")
	public void repleyOrder(Message<Order> messageOrder){
		log.info("Se llamo repleyOrder");
		MessageChannel repleyChanel=(MessageChannel) messageOrder.getHeaders().getReplyChannel();
		repleyChanel.send(messageOrder);
	}
	*/

}
