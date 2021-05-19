package com.rabobank.customerstatement.validator;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import com.rabobank.customerstatement.entity.CustomerStatementRequest;
import com.rabobank.customerstatement.entity.CustomerStatementResponse;
import com.rabobank.customerstatement.exceptions.InputValidationException;
import com.rabobank.customerstatement.exceptions.InvalidOperationException;
import com.rabobank.customerstatement.model.CustomerStatementRequestDto;
import com.rabobank.customerstatement.repository.CustomerStatementRepository;

@RunWith(SpringRunner.class)
public class CustomerStatementRequestValidatorTest {

  @TestConfiguration
  static class RequestValidatorTestConfiguration {

    @Bean
    public CustomerStatementRequestValidator customerStatementRequestValidator() {
      return new CustomerStatementRequestValidator();
    }

    @Bean
    public ModelMapper modelMapper() {
      return new ModelMapper();
    }
  }

  @Autowired
  private CustomerStatementRequestValidator customerStatementRequestValidator;

  @Autowired
  private ModelMapper modelMapper;

  @MockBean
  private CustomerStatementRepository customerStatementRepository;

  @Test
  public void validateTranRefAndEndBalancePositive() throws InvalidOperationException {
    CustomerStatementRequestDto customerStatementRequestDto = new CustomerStatementRequestDto();
    customerStatementRequestDto.setTransactionReference(873621L);
    customerStatementRequestDto.setAccountNumber("NL05RABO8372916472");
    customerStatementRequestDto.setStartBalance(62.8);
    customerStatementRequestDto.setMutationType("+3");
    customerStatementRequestDto.setDescription("Positive flow transaction.");
    customerStatementRequestDto.setEndBalance(65.8);

    CustomerStatementResponse customerStatementResponse = new CustomerStatementResponse();
    int resultCase = customerStatementRequestValidator.validateRefAndEndBalance(customerStatementRequestDto,
        customerStatementResponse);
    assertEquals(0, resultCase);
    assertEquals(0, customerStatementResponse.getErrorRecords().size());
  }

  @Test
  public void validateRefAndEndBalanceWhenEndBalanceNoMatch() throws InvalidOperationException {
    CustomerStatementRequestDto customerStatementRequestDto = new CustomerStatementRequestDto();
    customerStatementRequestDto.setTransactionReference(873621L);
    customerStatementRequestDto.setAccountNumber("NL05RABO8372916472");
    customerStatementRequestDto.setStartBalance(62.8);
    customerStatementRequestDto.setMutationType("-3");
    customerStatementRequestDto.setDescription("When End Balance does not match with Start & Mutation.");
    customerStatementRequestDto.setEndBalance(70.4);

    CustomerStatementResponse customerStatementResponse = new CustomerStatementResponse();
    int resultCase = customerStatementRequestValidator.validateRefAndEndBalance(customerStatementRequestDto,
        customerStatementResponse);
    assertEquals(2, resultCase);
    assertEquals(1, customerStatementResponse.getErrorRecords().size());
    assertEquals("INCORRECT_END_BALANCE", customerStatementResponse.getResult());
  }

  @Test
  public void validateRefAndEndBalanceWhenRecordAlreadyPresent() throws InvalidOperationException {
    CustomerStatementRequestDto customerStatementRequestDto = new CustomerStatementRequestDto();
    customerStatementRequestDto.setTransactionReference(873621L);
    customerStatementRequestDto.setAccountNumber("NL05ABNA123456789");
    customerStatementRequestDto.setStartBalance(69.8);
    customerStatementRequestDto.setMutationType("-5");
    customerStatementRequestDto.setDescription("When record is already pesent by its reference.");
    customerStatementRequestDto.setEndBalance(64.8);

    CustomerStatementResponse customerStatementResponse = new CustomerStatementResponse();

    Optional<CustomerStatementRequest> recordInDB =
        Optional.of(modelMapper.map(customerStatementRequestDto, CustomerStatementRequest.class));
    when(customerStatementRepository.findById(customerStatementRequestDto.getTransactionReference()))
        .thenReturn(recordInDB);
    int resultCase = customerStatementRequestValidator.validateRefAndEndBalance(customerStatementRequestDto,
        customerStatementResponse);

    assertEquals(1, resultCase);
    assertEquals(1, customerStatementResponse.getErrorRecords().size());
    assertEquals("DUPLICATE_REFERENCE", customerStatementResponse.getResult());
  }

  @Test
  public void validateRefAndEndBalanceWhenBothIncorrect() throws InvalidOperationException {
    CustomerStatementRequestDto customerStatementRequestDto = new CustomerStatementRequestDto();
    customerStatementRequestDto.setTransactionReference(871221L);
    customerStatementRequestDto.setAccountNumber("NL05ABNA123456789");
    customerStatementRequestDto.setStartBalance(69.8);
    customerStatementRequestDto.setMutationType("-5");
    customerStatementRequestDto
        .setDescription("When end balance is incorrect and transaction reference already present");
    customerStatementRequestDto.setEndBalance(64.1);

    CustomerStatementResponse customerStatementResponse = new CustomerStatementResponse();

    Optional<CustomerStatementRequest> recordInDB =
        Optional.of(modelMapper.map(customerStatementRequestDto, CustomerStatementRequest.class));
    when(customerStatementRepository.findById(customerStatementRequestDto.getTransactionReference()))
        .thenReturn(recordInDB);
    int resultCase = customerStatementRequestValidator.validateRefAndEndBalance(customerStatementRequestDto,
        customerStatementResponse);

    assertEquals(2, resultCase);
    assertEquals(2, customerStatementResponse.getErrorRecords().size());
    assertEquals("DUPLICATE_REFERENCE_INCORRECT_END_BALANCE", customerStatementResponse.getResult());
  }

  @Test(expected = InputValidationException.class)
  public void testValidateInputsPositive() throws InputValidationException {
    CustomerStatementRequestDto customerStatementRequestDto = new CustomerStatementRequestDto();
    customerStatementRequestDto.setTransactionReference(93387L);
    customerStatementRequestDto.setAccountNumber("NL03RABO025383922");
    customerStatementRequestDto.setStartBalance(70.0);
    customerStatementRequestDto.setDescription("Validating inputs");
    customerStatementRequestDto.setEndBalance(65.0);

    customerStatementRequestValidator.validateRequestInputDTO(customerStatementRequestDto);

  }
}
