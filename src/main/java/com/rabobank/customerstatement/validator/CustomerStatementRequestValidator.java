package com.rabobank.customerstatement.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rabobank.customerstatement.entity.CustomerStatementRequest;
import com.rabobank.customerstatement.entity.CustomerStatementResponse;
import com.rabobank.customerstatement.entity.ErrorRecords;
import com.rabobank.customerstatement.exceptions.InputValidationException;
import com.rabobank.customerstatement.exceptions.InvalidOperationException;
import com.rabobank.customerstatement.model.CustomerStatementRequestDto;
import com.rabobank.customerstatement.repository.CustomerStatementRepository;

/**
 * Validation class to check inputs.
 */
@Component
public class CustomerStatementRequestValidator {

  private static Logger logger = LoggerFactory.getLogger(CustomerStatementRequestValidator.class);

  @Autowired
  private CustomerStatementRepository customerStatementRepository;

  /**
   * Validating if transaction reference is unique and end balance is correct.
   * 
   * @param customerStatementRequestDto DTO as input.
   * @param customerRecordResponse output response.
   * @return integer value depicting scenarios.
   * @throws InvalidOperationException if invalid operation in input.
   */
  public int validateRefAndEndBalance(CustomerStatementRequestDto customerStatementRequestDto,
      CustomerStatementResponse customerRecordResponse) throws InvalidOperationException {
    int caseOfError = 0;
    List<ErrorRecords> errorRecords = new ArrayList<>();

    if (validateTransactionRefernce(customerStatementRequestDto)) {
      logger.error("Transaction reference is duplicate. Already present in DB.");
      caseOfError = 1;
      errorRecords.add(new ErrorRecords(customerStatementRequestDto.getTransactionReference(),
          customerStatementRequestDto.getAccountNumber()));
    }

    if (!validateEndBalance(customerStatementRequestDto)) {
      logger.error("End Balance is incorrect");
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

  private boolean validateEndBalance(CustomerStatementRequestDto customerStatementRequestDto)
      throws InvalidOperationException {
    char c = customerStatementRequestDto.getMutationType().charAt(0);
    double endCheck = 0;
    if (c == '+')
      endCheck = customerStatementRequestDto.getStartBalance()
          + Integer.parseInt(customerStatementRequestDto.getMutationType().substring(1));
    else if (c == '-')
      endCheck = customerStatementRequestDto.getStartBalance()
          - Integer.parseInt(customerStatementRequestDto.getMutationType().substring(1));
    else {
      logger.error("Invalid operation. Neither credit/debit.");
      throw new InvalidOperationException();
    }
    return (endCheck == customerStatementRequestDto.getEndBalance());
  }

  public void validateRequestInputDTO(CustomerStatementRequestDto customerStatementRequestDto)
      throws InputValidationException {
    if (!(customerStatementRequestDto != null && customerStatementRequestDto.getTransactionReference() > 0
        && null != customerStatementRequestDto.getAccountNumber()
        && !customerStatementRequestDto.getAccountNumber().isEmpty()
        && null != customerStatementRequestDto.getMutationType())) {
      throw new InputValidationException();
    }
  }

}
