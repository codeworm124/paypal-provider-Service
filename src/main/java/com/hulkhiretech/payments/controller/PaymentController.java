package com.hulkhiretech.payments.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hulkhiretech.payments.pojo.CreateOrderReq;
import com.hulkhiretech.payments.pojo.OrderResponse;
import com.hulkhiretech.payments.service.interfaces.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/orders")
public class PaymentController {
    
	private final PaymentService paymentServiceImpl;
	
	@PostMapping("/create")
	public OrderResponse createOrder(@RequestBody CreateOrderReq createOrderReq) {
	
		log.info("Creating order in paypal provider service"+
		"||createOrderReq:{}",createOrderReq);
		
		OrderResponse response=paymentServiceImpl.createorder(createOrderReq);
		log.info("Order creation response from service:{}"+response);
		return response;
	}
	
	@PostMapping("/{orderId}/captureOrder")
	public OrderResponse captureOrder(@PathVariable String orderId) {
		
		log.info("Capturing order in paypal provider service with OrderId:"+orderId);
		
		OrderResponse response=paymentServiceImpl.captureOrder(orderId);
		
		return response;
	}
	
	
}
