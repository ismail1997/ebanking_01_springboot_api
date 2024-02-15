package com.ismail.ebankingbackend.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ismail.ebankingbackend.enums.AccountStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

import static jakarta.persistence.EnumType.STRING;

@Data @AllArgsConstructor @NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE",length = 4,discriminatorType = DiscriminatorType.STRING)
//@Table(name = "bank_accounts")
public  class BankAccount {
    @Id
    private String id;
    private double balance;
    private Date createdAt;
    @Enumerated(STRING)
    private AccountStatus status;
    @ManyToOne
    private Customer customer;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToMany(mappedBy = "bankAccount",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccountOperation> accountOperations;
}
