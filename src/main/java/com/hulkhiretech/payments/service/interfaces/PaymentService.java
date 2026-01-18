package com.hulkhiretech.payments.service.interfaces;

import com.hulkhiretech.payments.pojo.CreateOrderReq;
import com.hulkhiretech.payments.pojo.OrderResponse;

public interface PaymentService {

	public OrderResponse createorder(CreateOrderReq createOrderReq);

	public OrderResponse captureOrder(String orderId);
}
