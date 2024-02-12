package com.ismail.ebankingbackend;

import com.ismail.ebankingbackend.entities.*;
import com.ismail.ebankingbackend.enums.AccountStatus;
import com.ismail.ebankingbackend.enums.OperationType;
import com.ismail.ebankingbackend.repositories.IAccountOperationRepository;
import com.ismail.ebankingbackend.repositories.IBankAccountRepository;
import com.ismail.ebankingbackend.repositories.ICustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class EbankingBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbankingBackendApplication.class, args);
    }


    @Bean
    CommandLineRunner start(ICustomerRepository customerRepository, IBankAccountRepository bankAccountRepository, IAccountOperationRepository accountOperationRepository){
        return args -> {
            Stream.of("Ismail","John","Smith").forEach(name->{
                Customer customer= Customer.builder()
                        .name(name)
                        .email(name+"@gmail.com")
                        .build();
                customerRepository.save(customer);
            });

            customerRepository.findAll().forEach(customer -> {
                CurrentAccount currentAccount = new CurrentAccount();
                currentAccount.setId(UUID.randomUUID().toString());
                currentAccount.setBalance(Math.random()*90000);
                currentAccount.setStatus(AccountStatus.CREATED);
                currentAccount.setCustomer(customer);
                currentAccount.setOverDraft(800);
                currentAccount.setCreatedAt(new Date());

                bankAccountRepository.save(currentAccount);

                SavingAccount savingAccount = new SavingAccount();
                savingAccount.setId(UUID.randomUUID().toString());
                savingAccount.setBalance(Math.random()*90000);
                savingAccount.setStatus(AccountStatus.CREATED);
                savingAccount.setCustomer(customer);
                savingAccount.setInterestRate(5.5);
                savingAccount.setCreatedAt(new Date());

                bankAccountRepository.save(savingAccount);

            });

            bankAccountRepository.findAll().forEach(bankAccount -> {
                for(int i =0 ; i<10 ; i++)
                {
                    AccountOperation accountOperation = AccountOperation.builder()
                            .bankAccount(bankAccount)
                            .amount(Math.random()*12000)
                            .type(Math.random()>0.5? OperationType.DEBIT:OperationType.CREDIT)
                            .operationDate(new Date())
                            .build();
                    accountOperationRepository.save(accountOperation);
                }
            });
        };
    }

}
