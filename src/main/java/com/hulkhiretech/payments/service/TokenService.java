package com.hulkhiretech.payments.service;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hulkhiretech.payments.constatnt.Constant;
import com.hulkhiretech.payments.http.HttpRequest;
import com.hulkhiretech.payments.http.HttpServiceEngine;
import com.hulkhiretech.payments.paypal.res.PayPalOAuthToken;
import com.hulkhiretech.payments.util.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenService {

	

	//TODO,implement Redis based and take care of expiry
	private static String accessToken;
	
	@Value("${paypal.client.id}")
	private String clientId;
	
	@Value("${paypal.client.secret}")
	private String clientSecret;
	
	@Value("${paypal.client.url}")
	private String outhUrl;
	
	private final HttpServiceEngine httpServiceEngine;
    private final JsonUtil jsonUtil;
	private final ObjectMapper objectMapper;
	
	public String getAccessToken() {
		log.info("retriving AccessToken from TokenService.");

		if(accessToken!=null) {
			log.info("Returning cached access token");
			return accessToken;
		}

		log.info("No cached access token found,calling OAuth serivce");


		HttpHeaders headers=new HttpHeaders();
		
		headers.setBasicAuth(clientId, clientSecret);
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			
		MultiValueMap<String,String> formData=new LinkedMultiValueMap<>();
		formData.add(Constant.GRANT_TYPE,Constant.CLIENT_CREDENTIALS);

		HttpRequest httpRequest=new HttpRequest();
		httpRequest.setBody(formData);
		httpRequest.setHttpHeaders(headers);
		httpRequest.setHttpMethod(HttpMethod.POST);
		httpRequest.setUrl(outhUrl);

		log.info("Prepared HttpRequest for OAuth call:{}",httpRequest);
		
		ResponseEntity<String> response=httpServiceEngine.makeHttpCall(httpRequest);
		String tokenBody=response.getBody();
		log.info("HTTP response from HttpServiceEngine:{}",tokenBody);
		
		
		PayPalOAuthToken token=jsonUtil.fromJson(tokenBody, PayPalOAuthToken.class);
		accessToken=token.getAccessToken();
		log.info("Caching access token for future use");
		
		return accessToken;
		
	}
}
