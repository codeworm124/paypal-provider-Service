package com.hulkhiretech.payments.service.helper;

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
import com.hulkhiretech.payments.paypal.res.PayPalOrder;
import com.hulkhiretech.payments.paypal.res.error.PayPalErrorResponse;
import com.hulkhiretech.payments.pojo.OrderResponse;
import com.hulkhiretech.payments.util.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CaptureOrderHelper {
	
	@Value("${paypal.capture.order.url}")
	private String captureOrderUrl;
	 private final JsonUtil jsonUtil;
	
	public HttpRequest prepareCaptureOrderHttpRequest(String orderId, String accessToken) {
		// TODO:prepare http request for capture order by set body,headers,url,http method
		
		log.info("Preparing capture order http request for orderId:{}"+orderId);
		
		//prepare headers
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken);
		headers.setContentType(MediaType.APPLICATION_JSON);
		String uuid=UUID.randomUUID().toString();
		headers.add(Constant.PAY_PAL_REQUEST_ID, uuid);
		
		String captureOrderUrlWithId=captureOrderUrl.replace("{orderId}", orderId);
		log.info("Capture order url with orderId:{}",captureOrderUrlWithId);
		
		String requestAsJson ="{}";
		//prepare http request
		HttpRequest httpRequest=new HttpRequest();
		httpRequest.setUrl(captureOrderUrlWithId);
		httpRequest.setHttpMethod(HttpMethod.POST);
		httpRequest.setHttpHeaders(headers); 
		httpRequest.setBody(requestAsJson); //TODO:set body if any
	
		log.info("Prepared capture order http request:{}",httpRequest);
		return httpRequest;
	}

	public OrderResponse handlePaypalResponse(ResponseEntity<String> httpResponse) {
		
		log.info("Handling paypal response in capture order helper:{}",httpResponse);
		
		// TODO :1.convert response from json to object
//		  PayPalOrder payPalOrder=jsonUtil.fromJson(httpResponse.getBody(), PayPalOrder.class);
//		  log.info("Converted PayPalOrder from response json:{}",payPalOrder);
//		  
//		  OrderResponse orderResponse=toOrderResponse(payPalOrder);
		
		if(httpResponse.getStatusCode().is2xxSuccessful()) {
			PayPalOrder payPalOrder=jsonUtil.fromJson(httpResponse.getBody(), PayPalOrder.class);
			log.info("Converted PayPalOrder from response json:{}",payPalOrder);
			
			OrderResponse orderResponse=toOrderResponse(payPalOrder);
			log.info("Mapped OrderResponse from PayPalOrder:{}",orderResponse);
			
			
			if(orderResponse!=null
					&& orderResponse.getPaypalStatus()!=null
					&& !orderResponse.getPaypalStatus().isEmpty()
					&& orderResponse.getOrderId()!=null
					&& !orderResponse.getOrderId().isEmpty()){
				
				log.info("Successfully captured order with OrderResponse:{}",orderResponse);
				return orderResponse;
				
			}
				log.error("Failed to capture order, invalid OrderResponse:{}",orderResponse);
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
		


	private OrderResponse toOrderResponse(PayPalOrder payPalOrder) {
		
		log.info("Mapping PayPalOrder to OrderResponse:{}",payPalOrder);
		
		OrderResponse orderResponse=new OrderResponse();
		orderResponse.setOrderId(payPalOrder.getId());
		orderResponse.setPaypalStatus(payPalOrder.getStatus());
		log.info("Mapped OrderResponse:{}",orderResponse);
	  		return orderResponse;
	}

	
}
