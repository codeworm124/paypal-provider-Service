package com.hulkhiretech.payments.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payments.constatnt.ErrorCodeEnum;
import com.hulkhiretech.payments.exception.PaypalProviderException;
import com.hulkhiretech.payments.pojo.CreateOrderReq;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PaymentValidator {

	public void validateCreateOrder(CreateOrderReq createOrderReq) {
		/*
		 * this method checks incoming CreateOrderReq for null/invalid values.
		 * if there is any validation error,it throws PaypalProviderException
           if no error ,then just runs til the end.so void return type
		 */
		if(createOrderReq==null) {
			throw new PaypalProviderException(ErrorCodeEnum.INVALID_REQUEST.getErrorCode(),
			          ErrorCodeEnum.INVALID_REQUEST.getErrorMessage()
			          ,HttpStatus.BAD_REQUEST);
		}
		
		if(createOrderReq.getAmount() == null ||
				createOrderReq.getAmount()<=0) {
			throw new PaypalProviderException(ErrorCodeEnum.INVALID_REQUEST.getErrorCode(),
			          ErrorCodeEnum.INVALID_REQUEST.getErrorMessage()
			          ,HttpStatus.BAD_REQUEST);
		}
		
		if(createOrderReq.getCurrencyCode()==null ||
				createOrderReq.getCurrencyCode().isEmpty()) {
			 throw new PaypalProviderException(ErrorCodeEnum.CURRENCY_CODE_REQUIRED.getErrorCode(),
			          ErrorCodeEnum.CURRENCY_CODE_REQUIRED.getErrorMessage()
			          ,HttpStatus.BAD_REQUEST);
		}
		
		//check returnUrl & cancelUrl
		if(createOrderReq.getReturnUrl()==null ||
				createOrderReq.getReturnUrl().isBlank()) {
			 throw new PaypalProviderException(ErrorCodeEnum.RETURN_URL_REQUIRED.getErrorCode(),
			          ErrorCodeEnum.RETURN_URL_REQUIRED.getErrorMessage()
			          ,HttpStatus.BAD_REQUEST);
		}
		
		if(createOrderReq.getCancelUrl()==null ||
				createOrderReq.getCancelUrl().isBlank()) {
			 throw new PaypalProviderException(ErrorCodeEnum.CANCEL_URL_REQUIRED.getErrorCode(),
			          ErrorCodeEnum.CANCEL_URL_REQUIRED.getErrorMessage()
			          ,HttpStatus.BAD_REQUEST);
		}
		
		
	}
}
