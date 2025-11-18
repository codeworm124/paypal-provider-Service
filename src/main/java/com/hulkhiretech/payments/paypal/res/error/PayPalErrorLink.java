package com.hulkhiretech.payments.paypal.res.error;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PayPalErrorLink {

    private String href;
    private String rel;

    @JsonProperty("encType")
    private String encType;
}
