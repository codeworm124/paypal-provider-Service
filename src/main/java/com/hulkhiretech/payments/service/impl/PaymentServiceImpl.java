package com.hulkhiretech.payments.service.impl;

import java.util.Collections;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hulkhiretech.payments.http.HttpRequest;
import com.hulkhiretech.payments.http.HttpServiceEngine;
import com.hulkhiretech.payments.paypal.req.Amount;
import com.hulkhiretech.payments.paypal.req.ExperienceContext;
import com.hulkhiretech.payments.paypal.req.OrderRequest;
import com.hulkhiretech.payments.paypal.req.PayPal;
import com.hulkhiretech.payments.paypal.req.PaymentSource;
import com.hulkhiretech.payments.paypal.req.PurchaseUnit;
import com.hulkhiretech.payments.paypal.res.PayPalOrder;
import com.hulkhiretech.payments.paypal.res.PaypalLink;
import com.hulkhiretech.payments.pojo.CreateOrderReq;
import com.hulkhiretech.payments.pojo.OrderResponse;
import com.hulkhiretech.payments.service.TokenService;
import com.hulkhiretech.payments.service.interfaces.PaymentService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	private static final String INTENT_CAPTURE = "CAPTURE";
	private static final String USER_ACTION_PAY_NOW = "PAY_NOW";
	private static final String SHIPPING_PREF_NO_SHIPPING = "NO_SHIPPING";
	private static final String LOGIN = "LOGIN";
	private static final String IMMEDIATE_PAYMENT_REQUIRED = "IMMEDIATE_PAYMENT_REQUIRED";
	private static final String TWO_DECIMAL_FORMAT = "%.2f";
	private static final String PAY_PAL_REQUEST_ID = "PayPal-Request-Id";
	
	@Value("${paypal.create.order.url}")
	private String createOrderUrl;
	
	private final ObjectMapper objectMapper;
	private final TokenService tokenService;
	private final HttpServiceEngine httpServiceEngine;
	@Override
	public OrderResponse createorder(CreateOrderReq createOrderReq) {
		//get accessToken
		String accessToken=tokenService.getAccessToken();
		log.info("creating order in paymentServceImpl");
		
		HttpHeaders headers=new HttpHeaders();
	
	    headers.setBearerAuth(accessToken);
	    headers.setContentType(MediaType.APPLICATION_JSON);
	     
	    //set headers paypal-request-id=>UUID
	    String uuid=UUID.randomUUID().toString();
	    log.info("generated UUID for PayPal-Request-Id:{}",uuid);
	    headers.add(PAY_PAL_REQUEST_ID, uuid);
	    
	    Amount amount = new Amount();
        amount.setCurrencyCode(createOrderReq.getCurrencyCode());
        
        //read amount from createOrderReq and convert to 2 decimal places format.
        String amtStr=String.format(TWO_DECIMAL_FORMAT,createOrderReq.getAmount());
        amount.setValue(amtStr);


        PurchaseUnit purchaseUnit = new PurchaseUnit();
        purchaseUnit.setAmount(amount);
        
	    ExperienceContext context = new ExperienceContext();
        context.setPaymentMethodPreference(IMMEDIATE_PAYMENT_REQUIRED);
        context.setLandingPage(LOGIN);
        context.setShippingPreference(SHIPPING_PREF_NO_SHIPPING);
        context.setUserAction(USER_ACTION_PAY_NOW);
        context.setReturnUrl(createOrderReq.getReturnUrl());
        context.setCancelUrl(createOrderReq.getCancelUrl());

        PayPal paypal = new PayPal();
        paypal.setExperienceContext(context);

        PaymentSource paymentSource = new PaymentSource();
        paymentSource.setPaypal(paypal);

       

        OrderRequest request = new OrderRequest();
        request.setIntent(INTENT_CAPTURE);
        request.setPurchaseUnits(Collections.singletonList(purchaseUnit));
        request.setPaymentSource(paymentSource);
        
        log.info("Constructed OrderRequest object:{}",request);
	    
        //convert to json string.
        String requestAsJson=null;
	    try {
	    	requestAsJson=objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(request);
			log.info("OrderRequest JSON:{}",requestAsJson);
			
		} catch (Exception e) {
		
			log.error("Error creating OrderRequest JSON:{}",e.getMessage(),e);
			throw new RuntimeException("Error creating OrderRequest JSON:{}",e);
			
		}
	    
	    
	   

		//prepare httpRequest
		HttpRequest httpRequest=new HttpRequest();
		httpRequest.setBody(requestAsJson);
		httpRequest.setHttpHeaders(headers);
		httpRequest.setHttpMethod(HttpMethod.POST);
		
		httpRequest.setUrl(createOrderUrl);

		log.info("Prepared HttpRequest for OAuth call:{}",httpRequest);
		
		//pass httpRequest into httpEngine
        ResponseEntity<String> successResponse=httpServiceEngine.makeHttpCall(httpRequest  );	    
         
        PayPalOrder payPalOrder=null;
        try {
		      payPalOrder=objectMapper.readValue(successResponse.getBody(), PayPalOrder.class);
		} catch (Exception e) {
			log.info("error parsing PaypalOrder response",e);
			throw new RuntimeException("Error parsing PaypalOrder response",e);
		} 
        
        OrderResponse orderResponse=toOrderResponse(payPalOrder);
        log.info("Converted OrderResponse:{}",orderResponse);
        
		return orderResponse;
	}
	
	public OrderResponse toOrderResponse(PayPalOrder paypalOrder) {
		
		log.info("Converting PaypalOrder to OrderResponse:{}",paypalOrder);
		
	    OrderResponse response = new OrderResponse();
	    response.setOrderId(paypalOrder.getId());
	    response.setPaypalStatus(paypalOrder.getStatus());

	    // Find redirect link from links list
	   String redirectLink= paypalOrder.getLinks().stream()
	            .filter(link -> "payer-action".equalsIgnoreCase(link.getRel()))
	            .findFirst()
	            .map(PaypalLink::getHref)
	            .orElse(null);
	            
	            response.setRedirectUrl(redirectLink);  
	            log.info("converted PaypalOrder to OrderResponse:{}",response);
	            return response;
	    }

	   
	

	
	@PostConstruct 
	public void init() {
		log.info("Payment service initialized");
	}

}
