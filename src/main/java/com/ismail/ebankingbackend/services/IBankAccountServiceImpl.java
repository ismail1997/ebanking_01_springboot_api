package com.ismail.ebankingbackend.services;

import com.ismail.ebankingbackend.entities.*;

import com.ismail.ebankingbackend.enums.AccountStatus;
import com.ismail.ebankingbackend.enums.OperationType;
import com.ismail.ebankingbackend.exceptions.BalanceNotSufficientException;
import com.ismail.ebankingbackend.exceptions.BankAccountNotFoundException;
import com.ismail.ebankingbackend.exceptions.CustomerNotFoundException;
import com.ismail.ebankingbackend.repositories.IAccountOperationRepository;
import com.ismail.ebankingbackend.repositories.IBankAccountRepository;
import com.ismail.ebankingbackend.repositories.ICustomerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;


@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class IBankAccountServiceImpl implements IBankAccountService {

    private ICustomerRepository customerRepository;
    private IBankAccountRepository bankAccountRepository;
    private IAccountOperationRepository accountOperationRepository;



    @Override
    public Customer saveCustomer(Customer customer) {
        log.info("Saving new customer");
        return customerRepository.save(customer);
    }

    @Override
    public Customer getCustomerByID(Long id) throws CustomerNotFoundException {
        return customerRepository.findById(id).orElseThrow(()->new CustomerNotFoundException("Can not find customer"));
    }

    @Override
    public CurrentAccount saveCurrentBankAccount(double initialBalance,double overDraft, Long customerID) throws CustomerNotFoundException {
        log.info("Saving new current account");
        CurrentAccount bankAccount=new CurrentAccount();

        bankAccount.setId(UUID.randomUUID().toString());
        bankAccount.setBalance(initialBalance);
        bankAccount.setCreatedAt(new Date());
        bankAccount.setOverDraft(overDraft);
        bankAccount.setStatus(AccountStatus.CREATED);

        Customer customer = getCustomerByID(customerID);
        if(customer==null){
            throw new CustomerNotFoundException("Customer not found");
        }else{
            bankAccount.setCustomer(customer);
        }




        return bankAccountRepository.save(bankAccount);
    }

    @Override
    public SavingAccount saveSavingBankAccount(double initialBalance, double interestRate, Long customerID) throws CustomerNotFoundException {
        log.info("Saving new saving account");
        SavingAccount bankAccount=new SavingAccount();

        bankAccount.setId(UUID.randomUUID().toString());
        bankAccount.setBalance(initialBalance);
        bankAccount.setCreatedAt(new Date());
        bankAccount.setInterestRate(interestRate);
        bankAccount.setStatus(AccountStatus.CREATED);

        Customer customer = getCustomerByID(customerID);
        if(customer==null){
            throw new CustomerNotFoundException("Customer not found");
        }else{
            bankAccount.setCustomer(customer);
        }
        return bankAccountRepository.save(bankAccount);
    }

    @Override
    public List<Customer> listCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public BankAccount getBankAccountByID(String accountID) throws BankAccountNotFoundException{
        BankAccount bankAccount = bankAccountRepository.findById(accountID)
                .orElseThrow(()->new BankAccountNotFoundException("Could not find any bank account with such id"));
        return bankAccount;
    }

    @Override
    public List<BankAccount> listBankAccounts() {
        return bankAccountRepository.findAll();
    }

    @Override
    public void debit(String accountID, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException {
        BankAccount bankAccount=getBankAccountByID(accountID);
        if(bankAccount.getBalance()<amount){
         throw new BalanceNotSufficientException("Balance not sufficient")   ;
        }

        AccountOperation accountOperation = AccountOperation.builder()
                .type(OperationType.DEBIT)
                .operationDate(new Date())
                .amount(amount)
                .description(description)
                .bankAccount(bankAccount)
                .build();

        accountOperationRepository.save(accountOperation);

        bankAccount.setBalance(bankAccount.getBalance()-amount);

        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void credit(String accountID, double amount, String description) throws BankAccountNotFoundException {
        BankAccount bankAccount=getBankAccountByID(accountID);

        AccountOperation accountOperation = AccountOperation.builder()
                .type(OperationType.CREDIT)
                .operationDate(new Date())
                .amount(amount)
                .description(description)
                .bankAccount(bankAccount)
                .build();

        accountOperationRepository.save(accountOperation);

        bankAccount.setBalance(bankAccount.getBalance()+amount);

        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException {
        debit(accountIdSource,amount,"Transfer to "+accountIdDestination);
        credit(accountIdDestination,amount,"Transfer from "+accountIdSource);
    }
}
