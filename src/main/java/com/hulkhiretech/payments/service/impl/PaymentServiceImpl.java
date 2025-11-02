package com.hulkhiretech.payments.service.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hulkhiretech.payments.http.HttpRequest;
import com.hulkhiretech.payments.http.HttpServiceEngine;
import com.hulkhiretech.payments.paypal.res.PayPalOrder;
import com.hulkhiretech.payments.pojo.CreateOrderReq;
import com.hulkhiretech.payments.pojo.OrderResponse;
import com.hulkhiretech.payments.service.TokenService;
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
	
	@Override
	public OrderResponse createorder(CreateOrderReq createOrderReq) {
		
		//step1.get accessToken
		String accessToken=tokenService.getAccessToken();
		log.info("creating order in paymentServceImpl");
		
		//step2.prepare request
		HttpRequest httpRequest = createOrderHelper.prepareCreateOrderHttpRequest(createOrderReq, accessToken);
        log.info("Prepared HttpRequest for OAuth call:{}",httpRequest);
		
		//pass httpRequest into httpEngine
        ResponseEntity<String> successResponse=httpServiceEngine.makeHttpCall(httpRequest  );	    
         
        
      //STEP3.PROCESS ON THAT API CALL RESPONSE.
        PayPalOrder payPalOrder=jsonUtil.fromJson(successResponse.getBody(),PayPalOrder.class);
        //TODO failure/TimeOut-proper response handling
        
        OrderResponse orderResponse=createOrderHelper.toOrderResponse(payPalOrder);
        log.info("Converted OrderResponse:{}",orderResponse);
        
		return orderResponse;
	}

		
}
