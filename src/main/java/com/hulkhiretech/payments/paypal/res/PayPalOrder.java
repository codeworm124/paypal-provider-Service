package com.hulkhiretech.payments.paypal.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PayPalOrder {

    @JsonProperty("id")
    private String id;

    @JsonProperty("status")
    private String status;

    @JsonProperty("payment_source")
    private PaymentSource paymentSource;

    @JsonProperty("links")
    private List<PaypalLink> links;
}