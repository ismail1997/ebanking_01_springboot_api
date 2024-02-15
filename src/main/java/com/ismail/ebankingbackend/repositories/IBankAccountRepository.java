package com.ismail.ebankingbackend.repositories;

import com.ismail.ebankingbackend.entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IBankAccountRepository extends JpaRepository<BankAccount,String> {

    @Query("select ac from BankAccount ac where ac.customer.id=?1")
    List<BankAccount> getBankAccountsByCustomer(Long id);
}
