package com.hulkhiretech.payments.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.hulkhiretech.payments.constatnt.ErrorCodeEnum;
import com.hulkhiretech.payments.pojo.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(PaypalProviderException.class)
	public ResponseEntity<ErrorResponse> handlePaypalException(PaypalProviderException ex){
		
		ErrorResponse error=new ErrorResponse(ex.getErrorCode(),ex.getErrorMessage());
		
		return new ResponseEntity<>(error,ex.getHttpStatus());
		
		
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception ex){
		
		ErrorResponse error=new ErrorResponse(ErrorCodeEnum.GENERIC_ERROR.getErrorCode(),ErrorCodeEnum.GENERIC_ERROR.getErrorMessage());
		
		return new ResponseEntity<>(error,HttpStatus.INTERNAL_SERVER_ERROR);
		
		
	}
}
