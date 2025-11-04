package com.hulkhiretech.payments.util;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class JsonUtil {
	
    private final ObjectMapper objectMapper;
    
    public String toJson(Object obj) {
    	
    	try {
			return objectMapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			log.error("Error converting object to JSoN");
			throw new RuntimeException("JSOn=N conversion error:"+e.getMessage());
		}
    }
    
    public <T> T fromJson(String jsonString, Class<T> clazz) {
        try {
            return objectMapper.readValue(jsonString, clazz);
        } catch (Exception e) {
            log.info("Error converting JSON to Java object: " + e.getMessage());
            throw new RuntimeException("JSON conversion erroe:"+e.getMessage());
            
        }
    }
}
