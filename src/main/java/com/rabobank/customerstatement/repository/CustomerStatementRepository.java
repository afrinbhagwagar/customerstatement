package com.rabobank.customerstatement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rabobank.customerstatement.entity.CustomerStatementRequest;

@Repository
public interface CustomerStatementRepository extends JpaRepository<CustomerStatementRequest, Long> {

}
