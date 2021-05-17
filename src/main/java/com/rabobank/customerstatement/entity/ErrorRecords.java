package com.rabobank.customerstatement.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Class specifying list of error records.
 */
@Getter
@Setter
@AllArgsConstructor
public class ErrorRecords {

  private long reference;
  private String accountNumber;
}
