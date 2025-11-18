package com.hulkhiretech.payments.service.helper;

import java.util.Collections;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payments.constatnt.Constant;
import com.hulkhiretech.payments.constatnt.ErrorCodeEnum;
import com.hulkhiretech.payments.exception.PaypalProviderException;
import com.hulkhiretech.payments.http.HttpRequest;
import com.hulkhiretech.payments.paypal.req.Amount;
import com.hulkhiretech.payments.paypal.req.ExperienceContext;
import com.hulkhiretech.payments.paypal.req.OrderRequest;
import com.hulkhiretech.payments.paypal.req.PayPal;
import com.hulkhiretech.payments.paypal.req.PaymentSource;
import com.hulkhiretech.payments.paypal.req.PurchaseUnit;
import com.hulkhiretech.payments.paypal.res.PayPalOrder;
import com.hulkhiretech.payments.paypal.res.PaypalLink;
import com.hulkhiretech.payments.paypal.res.error.PayPalErrorResponse;
import com.hulkhiretech.payments.pojo.CreateOrderReq;
import com.hulkhiretech.payments.pojo.OrderResponse;
import com.hulkhiretech.payments.util.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreateOrderHelper {

	@Value("${paypal.create.order.url}")
	private String createOrderUrl;

	private final JsonUtil jsonUtil;

	public HttpRequest prepareCreateOrderHttpRequest(CreateOrderReq createOrderReq, String accessToken) {
		//step2.prepare request
		HttpHeaders headers = prepareHeader(accessToken);
         
		String requestAsJson = prepareRequestBodyAsJson(createOrderReq);
		

        //STEP3.MAKE API CALL
		//prepare httpRequest
		HttpRequest httpRequest=new HttpRequest();
		httpRequest.setBody(requestAsJson);
		httpRequest.setHttpHeaders(headers);
		httpRequest.setHttpMethod(HttpMethod.POST);
		httpRequest.setUrl(createOrderUrl);
		return httpRequest;
	}

	private String prepareRequestBodyAsJson(CreateOrderReq createOrderReq) {
		Amount amount = new Amount();
		amount.setCurrencyCode(createOrderReq.getCurrencyCode());
             //TODO commented for testing
		
		//read amount from createOrderReq and convert to 2 decimal places format.
		String amtStr=String.format(Constant.TWO_DECIMAL_FORMAT,createOrderReq.getAmount());
		amount.setValue(amtStr);


		PurchaseUnit purchaseUnit = new PurchaseUnit();
		purchaseUnit.setAmount(amount);

		ExperienceContext context = new ExperienceContext();
		context.setPaymentMethodPreference(Constant.IMMEDIATE_PAYMENT_REQUIRED);
		context.setLandingPage(Constant.LOGIN);
		context.setShippingPreference(Constant.SHIPPING_PREF_NO_SHIPPING);
		context.setUserAction(Constant.USER_ACTION_PAY_NOW);
		context.setReturnUrl(createOrderReq.getReturnUrl());
		context.setCancelUrl(createOrderReq.getCancelUrl());

		PayPal paypal = new PayPal();
		paypal.setExperienceContext(context);

		PaymentSource paymentSource = new PaymentSource();
		paymentSource.setPaypal(paypal);



		OrderRequest request = new OrderRequest();
		request.setIntent(Constant.INTENT_CAPTURE);
		request.setPurchaseUnits(Collections.singletonList(purchaseUnit));
		request.setPaymentSource(paymentSource);
		
		log.info("Prepared OrderRequest object:{}",request);
		
		//convertto JSON string
		String requestAsJson=jsonUtil.toJson(request);
		return requestAsJson;
	}

	private HttpHeaders prepareHeader(String accessToken) {
		HttpHeaders headers=new HttpHeaders();
		headers.setBearerAuth(accessToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		//set headers paypal-request-id=>UUID
		String uuid=UUID.randomUUID().toString();
		log.info("generated UUID for PayPal-Request-Id:{}",uuid);
		headers.add(Constant.PAY_PAL_REQUEST_ID, uuid);
		return headers;
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

public OrderResponse handlePaypalResponse(ResponseEntity<String> httpResponse) {
	log.info("HTTP Status: {}", httpResponse.getStatusCode());
	log.info("Response Body: {}", httpResponse.getBody());
	
	

	
	//STEP3.PROCESS ON THAT API CALL RESPONSE.
	
	log.info("Handling PayPal response in PaymentServiceImpl:{}",httpResponse);
	
	 if(httpResponse.getStatusCode().is2xxSuccessful()) {
		 PayPalOrder payPalOrder=jsonUtil.fromJson(httpResponse.getBody(),PayPalOrder.class);
		    
		    OrderResponse orderResponse=toOrderResponse(payPalOrder);
		    log.info("Converted OrderResponse:{}",orderResponse);
		    log.info("HTTP Redirect url: {}", orderResponse.getRedirectUrl());
		    //if we get a valid response with PAYER_ACTION_REQUIRED status and url and id then only it is success else failed
		    if(orderResponse!=null
		    		&& orderResponse.getOrderId()!=null
		    		&& !orderResponse.getOrderId().isBlank()
		    		&& orderResponse.getPaypalStatus()!=null
		    		&& orderResponse.getPaypalStatus().equalsIgnoreCase(Constant.PAYER_ACTION_REQUIRED)
		    		&& orderResponse.getRedirectUrl()!=null
		    		&& !orderResponse.getRedirectUrl().isEmpty()) {
		    	log.info("Order created successfully with PayPal. OrderResponse:{}",orderResponse);
		    	return orderResponse;
		    }
		    log.error("Failed to create order with PayPal. Incomplete OrderResponse:{}",orderResponse);
	 }
	
		
	 
	   //if 4xx or 5xx then proper error 
	 if(httpResponse.getStatusCode().is4xxClientError() || 
			 httpResponse.getStatusCode().is5xxServerError()) {
		 
		 log.error("Received 4xx or 5xx error from PayPal");
		 log.info("Received error response from PayPal: HTTP Status: {}, Body: {}", 
				 httpResponse.getStatusCode(), httpResponse.getBody());
		 
		 PayPalErrorResponse payPalErrorResponse=jsonUtil.fromJson(httpResponse.getBody(),
				                               PayPalErrorResponse.class);
		 
		 log.info("PayPal error response details:{}",payPalErrorResponse);
		 log.error("Error response received from PAyPal service"+"Status Code:{},Response Body:{}",
				 httpResponse.getStatusCode(),httpResponse.getBody());
		 
		 String errorCode=ErrorCodeEnum.PAYPAL_ERROR.getErrorCode();
		 String errorMessage=payPalErrorResponse.getMessage();//TODO: 2hr 45 min day 3 week 5
		 
		 throw new PaypalProviderException(errorCode,
				 errorMessage,
				 HttpStatus.valueOf(httpResponse.getStatusCode().value()));
		 
	 }
	 
	 throw new PaypalProviderException(ErrorCodeEnum.PAYPAL_UNKNOWN_ERROR.getErrorCode(),
			 ErrorCodeEnum.PAYPAL_UNKNOWN_ERROR.getErrorMessage(),
			 HttpStatus.BAD_GATEWAY);
	 
}


}
