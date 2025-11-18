package com.hulkhiretech.payments.paypal.res.error;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PayPalErrorDetail {
    private String field;
    private String value;
    private String location;
    private String issue;
    private String description;
}