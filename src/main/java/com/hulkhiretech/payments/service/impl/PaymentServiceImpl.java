package com.hulkhiretech.payments.service.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hulkhiretech.payments.http.HttpRequest;
import com.hulkhiretech.payments.http.HttpServiceEngine;
import com.hulkhiretech.payments.pojo.CreateOrderReq;
import com.hulkhiretech.payments.pojo.OrderResponse;
import com.hulkhiretech.payments.service.PaymentValidator;
import com.hulkhiretech.payments.service.TokenService;
import com.hulkhiretech.payments.service.helper.CaptureOrderHelper;
import com.hulkhiretech.payments.service.helper.CreateOrderHelper;
import com.hulkhiretech.payments.service.interfaces.PaymentService;
import com.hulkhiretech.payments.util.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	
	private final ObjectMapper objectMapper;
	private final TokenService tokenService;
	private final HttpServiceEngine httpServiceEngine;
	private final CreateOrderHelper createOrderHelper;
	private final JsonUtil jsonUtil;
	private final PaymentValidator paymentValidator;
	
	private final CaptureOrderHelper captureOrderHelper;
	
	@Override
	public OrderResponse createorder(CreateOrderReq createOrderReq) {
		//step0.validate request
		paymentValidator.validateCreateOrder(createOrderReq);
		
		//step1.get accessToken
		String accessToken=tokenService.getAccessToken();
		log.info("creating order in paymentServceImpl");
		
		//step2.prepare request
		HttpRequest httpRequest = createOrderHelper.prepareCreateOrderHttpRequest(createOrderReq, accessToken);
        log.info("Prepared HttpRequest for OAuth call:{}",httpRequest);
		
		//step3.MAKE API CALL
        ResponseEntity<String> httpResponse=httpServiceEngine.makeHttpCall(httpRequest);	    
        log.info("Success response from HttpServiceEngine:{}",httpResponse);
        
        //step4.handle response
      OrderResponse orderResponse =createOrderHelper.handlePaypalResponse(httpResponse);
      log.info("Final OrderResponse from createorder:{}",orderResponse);  
      
		return orderResponse;
	}

	@Override
	public OrderResponse captureOrder(String orderId) {
		
		//TODO:
				/*
				 * 1.Token generation
				 * 2.prepare http request for capture order
				 * 3.make API call to paypal by using HttpServiceEngine
				 * */
		
		
		//step 1 token generation
		String accessToken=tokenService.getAccessToken();
		
		//step2 prepare http request for capture order
		HttpRequest httpRequest=captureOrderHelper.prepareCaptureOrderHttpRequest(orderId, accessToken);
		log.info("Prepared HttpRequest for capture order:{}",httpRequest);
		
		//step3. make API call to paypal using HttpServiceEngine
		ResponseEntity<String> httpResponse=httpServiceEngine.makeHttpCall(httpRequest);	    
		log.info("Success response from HttpServiceEngine for capture order:{}",httpResponse);
		
		//process response
		OrderResponse orderResponse=captureOrderHelper.handlePaypalResponse(httpResponse);
		log.info("Final OrderResponse from capture order:{}",orderResponse);
		
		return orderResponse;
	}
}
	
		

