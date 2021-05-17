package com.rabobank.customerstatement.exceptions;

import java.util.ArrayList;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.rabobank.customerstatement.entity.CustomerStatementResponse;

/**
 * Exception class to handle different scenarios
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class CustomerStatementCustomExceptions extends ResponseEntityExceptionHandler{

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
    CustomerStatementResponse customerStatementResponse = new CustomerStatementResponse();
    customerStatementResponse.setResult("BAD_REQUEST");
    customerStatementResponse.setErrorRecords(new ArrayList<>());
    
    return new ResponseEntity<>(customerStatementResponse, status);
  }
  
  @ExceptionHandler({InvalidOperationException.class, Exception.class})
  public ResponseEntity<CustomerStatementResponse> handleGenericExceptions() {
    CustomerStatementResponse customerStatementResponse = new CustomerStatementResponse();
    customerStatementResponse.setResult("INTERNAL_SERVER_ERROR");
    customerStatementResponse.setErrorRecords(new ArrayList<>());
    
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(customerStatementResponse);
  }
}
