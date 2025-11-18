package com.hulkhiretech.payments.constatnt;

import lombok.Getter;

@Getter
public enum ErrorCodeEnum {

	GENERIC_ERROR("3000","Something went wrong .Please try again later"),
	CURRENCY_CODE_REQUIRED("3001","Currency code is required field and cannot be null/blank"),
	RETURN_URL_REQUIRED("3002","Return URL is required field and cannot be null/blank"),
	INVALID_REQUEST("3003","invalid request payload"),
	INVALID_AMOUNT("3004","Amount must be greater than zero."),
	CANCEL_URL_REQUIRED("3005","Return URL is required field and cannot be null/blank"),
	PAYPAL_SERVICE_UNAVAILABLE("3006","PayPal service is currently unavailable .Please try again later"),
	PAYPAL_ERROR("3007","<Error as Paypal>"), 
	PAYPAL_UNKNOWN_ERROR("3008","Unknown error occurred while processing PayPal request");
	
	
	private final String errorCode;
	private final String errorMessage;
	
	ErrorCodeEnum(String errorCode,String errorMessage){
		this.errorCode=errorCode;
		this.errorMessage=errorMessage;
	}
}
