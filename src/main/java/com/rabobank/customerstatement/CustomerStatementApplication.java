package com.rabobank.customerstatement;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * The starter class.
 */
@SpringBootApplication
public class CustomerStatementApplication {

  /**
   * Method to start execution.
   * 
   * @param args input arguments if any.
   */
  public static void main(String[] args) {
    SpringApplication.run(CustomerStatementApplication.class, args);
  }

  /**
   * For Input DTO to Entity or vice versa conversion
   * 
   * @return ModelMapper
   */
  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }
}
