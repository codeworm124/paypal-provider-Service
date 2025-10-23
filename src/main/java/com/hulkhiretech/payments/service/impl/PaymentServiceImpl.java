package com.hulkhiretech.payments.service.impl;

import org.springframework.stereotype.Service;

import com.hulkhiretech.payments.service.interfaces.PaymentService;

import jakarta.annotation.PostConstruct;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

	@Override
	public String createorder() {
		log.info("creating order in paymentServceImpl");
		return "Order created from service";
	}
	
	@PostConstruct
	public void init() {
		log.info("Payment service initialized");
	}

}
