package com.ismail.ebankingbackend.repositories;

import com.ismail.ebankingbackend.entities.AccountOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAccountOperationRepository extends JpaRepository<AccountOperation,Long> {
}
