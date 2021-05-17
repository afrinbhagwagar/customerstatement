package com.rabobank.customerstatement.model;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO class as request input object
 *
 */
@Getter
@Setter
public class CustomerStatementRequestDto {

  private long transactionReference;
  private String accountNumber;
  private double startBalance;
  private String mutationType;
  private String description;
  private double endBalance;

  public CustomerStatementRequestDto() {
    //DTO object creation
  }
}
