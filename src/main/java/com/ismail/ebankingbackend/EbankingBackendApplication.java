package com.ismail.ebankingbackend;

import com.ismail.ebankingbackend.entities.*;
import com.ismail.ebankingbackend.enums.OperationType;
import com.ismail.ebankingbackend.exceptions.CustomerNotFoundException;
import com.ismail.ebankingbackend.services.IBankAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

@SpringBootApplication
public class EbankingBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbankingBackendApplication.class, args);
    }


    @Bean
    CommandLineRunner start(IBankAccountService bankAccountService) {
        return args -> {
            Stream.of("Ismail", "John", "Smith").forEach(name -> {
                Customer customer = Customer.builder()
                        .name(name)
                        .email(name + "@gmail.com")
                        .build();
                bankAccountService.saveCustomer(customer);
            });

            bankAccountService.listCustomers().forEach(customer -> {

                try {
                    bankAccountService.saveCurrentBankAccount(Math.random() * 9000, 700, customer.getId());
                    bankAccountService.saveSavingBankAccount(Math.random() * 9000, 5.5, customer.getId());
                    List<BankAccount> bankAccounts = bankAccountService.listBankAccounts();
                    for(BankAccount bankAccount : bankAccounts)
                    {
                        for(int i=0;i<10;i++)
                        {
                            bankAccountService.credit(bankAccount.getId(), 12000,"Credit");
                            bankAccountService.debit(bankAccount.getId(), 200,"Debit");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });


        };
    }

}
