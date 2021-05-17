package com.rabobank.customerstatement.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rabobank.customerstatement.entity.CustomerStatementRequest;
import com.rabobank.customerstatement.entity.CustomerStatementResponse;
import com.rabobank.customerstatement.entity.ErrorRecords;
import com.rabobank.customerstatement.exceptions.InvalidOperationException;
import com.rabobank.customerstatement.model.CustomerStatementRequestDto;
import com.rabobank.customerstatement.repository.CustomerStatementRepository;

@Component
public class CustomerStatementRequestValidator {

  @Autowired
  private CustomerStatementRepository customerStatementRepository;

  public int validateRefAndEndBalance(CustomerStatementRequestDto customerStatementRequestDto, CustomerStatementResponse customerRecordResponse)
      throws InvalidOperationException {
    int caseOfError = 0;
    List<ErrorRecords> errorRecords = new ArrayList<>();

    if (validateTransactionRefernce(customerStatementRequestDto)) {
      caseOfError = 1;
      errorRecords.add(new ErrorRecords(customerStatementRequestDto.getTransactionReference(),
          customerStatementRequestDto.getAccountNumber()));
    }

    if (!validateEndBalance(customerStatementRequestDto)) {
      caseOfError = 2;
      errorRecords.add(new ErrorRecords(customerStatementRequestDto.getTransactionReference(),
          customerStatementRequestDto.getAccountNumber()));
    }

    setResultAndErrorRecords(customerRecordResponse, caseOfError, errorRecords);
    return caseOfError;
  }

  private void setResultAndErrorRecords(CustomerStatementResponse customerStatementResponse, int caseOfError,
      List<ErrorRecords> errorRecords) {
    if (errorRecords.size() == 2) {
      customerStatementResponse.setResult("DUPLICATE_REFERENCE_INCORRECT_END_BALANCE");
    } else if (errorRecords.size() == 1 && caseOfError == 1) {
      customerStatementResponse.setResult("DUPLICATE_REFERENCE");
    } else if (errorRecords.size() == 1 && caseOfError == 2) {
      customerStatementResponse.setResult("INCORRECT_END_BALANCE");
    }
    customerStatementResponse.setErrorRecords(errorRecords);
  }
  
  private boolean validateTransactionRefernce(CustomerStatementRequestDto customerStatementRequestDto) {
    Optional<CustomerStatementRequest> recordById =
        customerStatementRepository.findById(customerStatementRequestDto.getTransactionReference());
    return recordById.isPresent();
  }
  
  private boolean validateEndBalance(CustomerStatementRequestDto customerStatementRequestDto) throws InvalidOperationException {
    char c = customerStatementRequestDto.getMutationType().charAt(0);
    double endCheck = 0;
    if (c == '+')
      endCheck = customerStatementRequestDto.getStartBalance()
          + Integer.parseInt(customerStatementRequestDto.getMutationType().substring(1));
    else if (c == '-')
      endCheck = customerStatementRequestDto.getStartBalance()
          - Integer.parseInt(customerStatementRequestDto.getMutationType().substring(1));
    else
      throw new InvalidOperationException();
    return (endCheck == customerStatementRequestDto.getEndBalance());
  }

}
