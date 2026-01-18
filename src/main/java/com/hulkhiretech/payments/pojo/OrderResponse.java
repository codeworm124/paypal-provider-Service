package com.hulkhiretech.payments.pojo;

import lombok.Data;

@Data
public class OrderResponse {

    private String orderId;
    private String paypalStatus;
    private String redirectUrl;
}
