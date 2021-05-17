package com.rabobank.customerstatement.entity;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Response for POST/GET operations
 */
@Setter
@Getter
public class CustomerStatementResponse {

  public CustomerStatementResponse() {
    // Creating response object
  }
  
  private String result;
  private List<ErrorRecords> errorRecords;
}
