package com.training.thymeleafmid.config;

import org.springframework.web.bind.annotation.ExceptionHandler;

@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(Exception.class)
    public String handleException(Exception e) {
        return "redirect:/login";
    }
}
