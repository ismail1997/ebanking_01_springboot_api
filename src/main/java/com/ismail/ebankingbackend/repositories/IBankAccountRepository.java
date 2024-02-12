package com.ismail.ebankingbackend.repositories;

import com.ismail.ebankingbackend.entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IBankAccountRepository extends JpaRepository<BankAccount,String> {
}
