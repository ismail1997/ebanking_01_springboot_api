package com.ismail.ebankingbackend.repositories;

import com.ismail.ebankingbackend.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICustomerRepository extends JpaRepository<Customer,Long> {

    @Query("select c from Customer c where c.name like :kw ")
    List<Customer> searchCustomers(@Param(value = "kw") String keyword);
}
