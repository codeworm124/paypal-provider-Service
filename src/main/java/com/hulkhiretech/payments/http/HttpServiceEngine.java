package com.hulkhiretech.payments.http;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import com.hulkhiretech.payments.constatnt.ErrorCodeEnum;
import com.hulkhiretech.payments.exception.PaypalProviderException;
import com.hulkhiretech.payments.paypal.res.error.PayPalErrorResponse;
import com.hulkhiretech.payments.util.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class HttpServiceEngine {

	
	private final RestClient restClient;

	public ResponseEntity<String> makeHttpCall(HttpRequest httpRequest) {
		log.info("making http call in HttpServiceEngine");


		

		try {

			ResponseEntity<String> httpResponse=restClient.method(httpRequest.getHttpMethod())
					.uri(httpRequest.getUrl())
					.headers(restClientHeaders->restClientHeaders.addAll(httpRequest.getHttpHeaders()))
					.body(httpRequest.getBody())
					.retrieve()
					.toEntity(String.class);
			log.info("HTTP call completed httpResponse:{}",httpResponse);
			return httpResponse;
		}
		
		catch(HttpClientErrorException | HttpServerErrorException e) {
			log.error("HttpClientErrorException | HttpServerErrorException while preparing form data:{}",e.getMessage(),e);
			
			//if error is 503 or 504 then throw paypalproviderexception
			if(e.getStatusCode()==HttpStatus.GATEWAY_TIMEOUT ||
					e.getStatusCode()==HttpStatus.SERVICE_UNAVAILABLE) {
				log.info("Recieved 503 or 504 from paypal");
				log.error("Service is unavilable or gateway timeout");
				
			
				throw new PaypalProviderException(ErrorCodeEnum.PAYPAL_SERVICE_UNAVAILABLE.getErrorCode(), 
						ErrorCodeEnum.PAYPAL_SERVICE_UNAVAILABLE.getErrorMessage(), 
						HttpStatus.SERVICE_UNAVAILABLE);
			}
			
			
			//return responseEntity with error detail
			String errorResponse = e.getResponseBodyAsString();
			return ResponseEntity.status(e.getStatusCode())
					.body(errorResponse);
		}
		
		catch(Exception e) { //no response case
			log.error("Exception while preparing form data:{}",e.getMessage(),e);
			throw new PaypalProviderException(ErrorCodeEnum.PAYPAL_SERVICE_UNAVAILABLE.getErrorCode()
					, ErrorCodeEnum.PAYPAL_SERVICE_UNAVAILABLE.getErrorMessage()
					,HttpStatus.SERVICE_UNAVAILABLE); 
		}

	}
}
