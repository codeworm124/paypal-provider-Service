package com.hulkhiretech.payments.paypal.res.error;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PayPalErrorResponse {

    private String name;
    private String message;

    @JsonProperty("debug_id")
    private String debugId;

    // For OAuth-style error format
    private String error;

    @JsonProperty("error_description")
    private String errorDescription;
    
    // Nested lists
    private List<PayPalErrorDetail> details;
    private List<PayPalErrorLink> links;
}