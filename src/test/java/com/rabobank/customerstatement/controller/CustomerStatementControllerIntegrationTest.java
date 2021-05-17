package com.rabobank.customerstatement.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabobank.customerstatement.model.CustomerStatementRequestDto;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CustomerStatementControllerIntegrationTest {

  private static final String CONTEXT_ROOT = "/customerstatement";
  private static final String CONTENT_TYPE = "application/json";

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void testPostPositive() throws Exception {
    CustomerStatementRequestDto customerStatementRequestDto = dtoForPositiveFlow();

    mockMvc
        .perform(post(CONTEXT_ROOT).contentType(CONTENT_TYPE)
            .content(new ObjectMapper().writeValueAsString(customerStatementRequestDto)))
        .andExpect(status().isOk())
        .andExpect(content().string(equalTo("{\"result\":\"SUCCESSFUL\",\"errorRecords\":[]}")));
  }

  @Test
  public void testPostWhenEndBalanceIncorrect() throws Exception {
    CustomerStatementRequestDto customerStatementRequestDto = dtoForIncorrectEndBalance();

    mockMvc
        .perform(post(CONTEXT_ROOT).contentType(CONTENT_TYPE)
            .content(new ObjectMapper().writeValueAsString(customerStatementRequestDto)))
        .andExpect(status().isOk()).andExpect(content().string(equalTo(
            "{\"result\":\"INCORRECT_END_BALANCE\",\"errorRecords\":[{\"reference\":871221,\"accountNumber\":\"NL05RABO382971916472\"}]}")));
  }

  @Test
  public void testPostWhenReferenceAlreadyPresent() throws Exception {
    CustomerStatementRequestDto correctDTO = inputCorrectDto();

    mockMvc
        .perform(
            post(CONTEXT_ROOT).contentType(CONTENT_TYPE).content(new ObjectMapper().writeValueAsString(correctDTO)))
        .andExpect(status().isOk());

    CustomerStatementRequestDto dtoForReferencePresent = inputDtoForReferencePresent();

    mockMvc
        .perform(post(CONTEXT_ROOT).contentType(CONTENT_TYPE)
            .content(new ObjectMapper().writeValueAsString(dtoForReferencePresent)))
        .andExpect(status().isOk()).andExpect(content().string(equalTo(
            "{\"result\":\"DUPLICATE_REFERENCE\",\"errorRecords\":[{\"reference\":873621,\"accountNumber\":\"NL05RABO382972916472\"}]}")));
  }

  @Test
  public void testPostWhenJsonParseException() throws Exception {

    mockMvc.perform(post(CONTEXT_ROOT).contentType(CONTENT_TYPE)
        .content("{\r\n" + "    \"transactionReference\": 854,\r\n" + "    \"accountNumber\": \"NLABNA271827189\",\r\n"
            + "    \"startBalance\": 87.0,\r\n" + "    \"mutationType\": \"+4\",\r\n"
            + "    \"description\": My first transaction\",\r\n" + "    \"endBalance\": 91.0\r\n" + "}"))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(equalTo("{\"result\":\"BAD_REQUEST\",\"errorRecords\":[]}")));
  }
  
  @Test
  public void testPostWhenSomeGenericExceptions() throws Exception {
    CustomerStatementRequestDto customerStatementRequestDto = dtoForInvalidOperationException();

    mockMvc
        .perform(post(CONTEXT_ROOT).contentType(CONTENT_TYPE)
            .content(new ObjectMapper().writeValueAsString(customerStatementRequestDto)))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string(equalTo("{\"result\":\"INTERNAL_SERVER_ERROR\",\"errorRecords\":[]}")));
  }

  private CustomerStatementRequestDto dtoForPositiveFlow() {
    CustomerStatementRequestDto customerStatementRequestDto = new CustomerStatementRequestDto();
    customerStatementRequestDto.setTransactionReference(873114921L);
    customerStatementRequestDto.setAccountNumber("NL05RABO8372916472");
    customerStatementRequestDto.setStartBalance(62.8);
    customerStatementRequestDto.setMutationType("+3");
    customerStatementRequestDto.setDescription("Positive flow transaction.");
    customerStatementRequestDto.setEndBalance(65.8);
    return customerStatementRequestDto;
  }

  private CustomerStatementRequestDto dtoForIncorrectEndBalance() {
    CustomerStatementRequestDto customerStatementRequestDto = new CustomerStatementRequestDto();
    customerStatementRequestDto.setTransactionReference(871221L);
    customerStatementRequestDto.setAccountNumber("NL05RABO382971916472");
    customerStatementRequestDto.setStartBalance(62.8);
    customerStatementRequestDto.setMutationType("-3");
    customerStatementRequestDto.setDescription("End balance incorrect");
    customerStatementRequestDto.setEndBalance(65.8);
    return customerStatementRequestDto;
  }

  private CustomerStatementRequestDto inputDtoForReferencePresent() {
    CustomerStatementRequestDto dtoForReferencePresent = new CustomerStatementRequestDto();
    dtoForReferencePresent.setTransactionReference(873621L);
    dtoForReferencePresent.setAccountNumber("NL05RABO382972916472");
    dtoForReferencePresent.setStartBalance(62.8);
    dtoForReferencePresent.setMutationType("+3");
    dtoForReferencePresent.setDescription("Transaction reference present.");
    dtoForReferencePresent.setEndBalance(65.8);
    return dtoForReferencePresent;
  }

  private CustomerStatementRequestDto inputCorrectDto() {
    CustomerStatementRequestDto customerStatementRequestDto = new CustomerStatementRequestDto();
    customerStatementRequestDto.setTransactionReference(873621L);
    customerStatementRequestDto.setAccountNumber("NL05RABO382972916472");
    customerStatementRequestDto.setStartBalance(62.8);
    customerStatementRequestDto.setMutationType("+3");
    customerStatementRequestDto.setDescription("Transaction reference present.");
    customerStatementRequestDto.setEndBalance(65.8);
    return customerStatementRequestDto;
  }
  
  private CustomerStatementRequestDto dtoForInvalidOperationException() {
    CustomerStatementRequestDto customerStatementRequestDto = new CustomerStatementRequestDto();
    customerStatementRequestDto.setTransactionReference(873621L);
    customerStatementRequestDto.setAccountNumber("NL05RABO382972916472");
    customerStatementRequestDto.setStartBalance(62.8);
    customerStatementRequestDto.setMutationType("*3");
    customerStatementRequestDto.setDescription("Transaction reference present.");
    customerStatementRequestDto.setEndBalance(65.8);
    return customerStatementRequestDto;
  }


}
