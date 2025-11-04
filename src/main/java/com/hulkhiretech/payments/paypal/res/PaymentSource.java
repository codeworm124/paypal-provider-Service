package com.hulkhiretech.payments.paypal.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PaymentSource {

    @JsonProperty("paypal")
    private PayPal paypal;
}
