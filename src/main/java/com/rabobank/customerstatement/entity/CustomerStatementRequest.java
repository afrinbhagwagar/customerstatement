package com.rabobank.customerstatement.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class CustomerStatementRequest {

  @Id
  private long transactionReference;

  private String accountNumber;
  private double startBalance;
  private String mutationType;
  private String description;
  private double endBalance;

  public CustomerStatementRequest() {
    // Creating request object
  }
}
