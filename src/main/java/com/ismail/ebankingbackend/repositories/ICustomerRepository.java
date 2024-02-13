package com.ismail.ebankingbackend.repositories;

import com.ismail.ebankingbackend.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICustomerRepository extends JpaRepository<Customer,Long> {

    List<Customer> findByNameContains(String keyword);
}
