package com.rabobank.customerstatement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rabobank.customerstatement.entity.CustomerStatementResponse;
import com.rabobank.customerstatement.exceptions.InvalidOperationException;
import com.rabobank.customerstatement.model.CustomerStatementRequestDto;
import com.rabobank.customerstatement.service.CustomerStatementService;
import com.rabobank.customerstatement.validator.CustomerStatementRequestValidator;

@RestController
@RequestMapping("/customerstatement")
public class CustomerStatementController {

  @Autowired
  private CustomerStatementRequestValidator customerStatementRequestValidator;

  @Autowired
  private CustomerStatementService customerStatementService;

  /**
   * To save customer statements.
   * 
   * @param customerStatementRequestDto input DTO with customer transaction details.
   * @return response if saved or not.
   * @throws InvalidOperationException if operation is either debit/credit.
   */
  @PostMapping
  public ResponseEntity<CustomerStatementResponse> saveCustomerStatement(
      @RequestBody CustomerStatementRequestDto customerStatementRequestDto) throws InvalidOperationException {

    CustomerStatementResponse customerStatementResponse = new CustomerStatementResponse();
    int caseOfError = customerStatementRequestValidator.validateRefAndEndBalance(customerStatementRequestDto,
        customerStatementResponse);
    if (caseOfError != 0)
      return ResponseEntity.ok(customerStatementResponse);

    customerStatementService.saveCustomerStatement(customerStatementRequestDto);
    customerStatementResponse.setResult("SUCCESSFUL");

    return ResponseEntity.ok(customerStatementResponse);
  }

  /**
   * To get all customer statements.
   * 
   * @return list of all customer statements.
   */
  @GetMapping
  public List<CustomerStatementRequestDto> getAllCustomerStatements() {
    return customerStatementService.getAllCustomerStatements();
  }

}
