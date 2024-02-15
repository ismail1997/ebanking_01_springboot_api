package com.ismail.ebankingbackend.repositories;

import com.ismail.ebankingbackend.entities.AccountOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAccountOperationRepository extends JpaRepository<AccountOperation,Long> {
    List<AccountOperation> findByBankAccountId(String id);

    @Query("select operation from AccountOperation  operation where operation.bankAccount.id =?1 order by operation.operationDate desc ")
    Page<AccountOperation> findByBankAccountId(String id, Pageable pageable);
}
