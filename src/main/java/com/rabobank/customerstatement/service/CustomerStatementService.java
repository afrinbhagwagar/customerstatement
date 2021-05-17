package com.rabobank.customerstatement.service;

import java.util.List;

import com.rabobank.customerstatement.entity.CustomerStatementRequest;
import com.rabobank.customerstatement.model.CustomerStatementRequestDto;

public interface CustomerStatementService {

  CustomerStatementRequest saveCustomerStatement(CustomerStatementRequestDto customerStatementRequestDto);

  List<CustomerStatementRequestDto> getAllCustomerStatements();

}
