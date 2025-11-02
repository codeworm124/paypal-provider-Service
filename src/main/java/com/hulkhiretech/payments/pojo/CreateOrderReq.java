package com.hulkhiretech.payments.pojo;

import lombok.Data;

@Data
public class CreateOrderReq {

	private String currencyCode;
	private double amount;//later convert it into String
	private String returnUrl;
	private String cancelUrl;
}
