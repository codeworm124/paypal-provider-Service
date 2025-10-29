package com.hulkhiretech.payments.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hulkhiretech.payments.service.interfaces.PaymentService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PaymentController {
    
	private final PaymentService paymentServiceImpl;
	
	@PostMapping("/payments")
	public String createOrder() {
		log.info("Creating order in paypal provider service");
		
		return paymentServiceImpl.createorder();
	}
	@PostConstruct
	void init() {
		log.info("PaymentController initialized"+"paymentService:{}",paymentServiceImpl);
	}
}
