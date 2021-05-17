package com.rabobank.customerstatement.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rabobank.customerstatement.entity.CustomerStatementRequest;
import com.rabobank.customerstatement.model.CustomerStatementRequestDto;
import com.rabobank.customerstatement.repository.CustomerStatementRepository;

/**
 * Service implementation class.
 */
@Service
public class CustomerStatementServiceImpl implements CustomerStatementService {

  @Autowired
  private CustomerStatementRepository customerStatementRepository;

  @Autowired
  private ModelMapper modelMapper;

  @Override
  public CustomerStatementRequest saveCustomerStatement(CustomerStatementRequestDto customerStatementRequestDto) {
    return customerStatementRepository.save(modelMapper.map(customerStatementRequestDto, CustomerStatementRequest.class));
  }

  @Override
  public List<CustomerStatementRequestDto> getAllCustomerStatements() {
    return customerStatementRepository.findAll().stream().map(user -> modelMapper.map(user, CustomerStatementRequestDto.class))
        .collect(Collectors.toList());
  }

}
