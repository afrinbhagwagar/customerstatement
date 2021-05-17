package com.rabobank.customerstatement.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorRecords {

  private long reference;
  private String accountNumber;
}
