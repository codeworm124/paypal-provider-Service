package com.hulkhiretech.payments.paypal.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PurchaseUnit {

    @JsonProperty("amount")
    private Amount amount;
}

