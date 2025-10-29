package com.hulkhiretech.payments.service.impl;

import org.springframework.stereotype.Service;

import com.hulkhiretech.payments.service.TokenService;
import com.hulkhiretech.payments.service.interfaces.PaymentService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	private final TokenService tokenService;
	@Override
	public String createorder() {
		String accessToken=tokenService.getAccessToken();
		log.info("creating order in paymentServceImpl");
		return "Order created from service"+accessToken;
	}
	
	@PostConstruct
	public void init() {
		log.info("Payment service initialized");
	}

}
