package com.ismail.ebankingbackend.services;

import com.ismail.ebankingbackend.entities.BankAccount;
import com.ismail.ebankingbackend.entities.CurrentAccount;
import com.ismail.ebankingbackend.entities.Customer;
import com.ismail.ebankingbackend.entities.SavingAccount;
import com.ismail.ebankingbackend.exceptions.BalanceNotSufficientException;
import com.ismail.ebankingbackend.exceptions.BankAccountNotFoundException;
import com.ismail.ebankingbackend.exceptions.CustomerNotFoundException;

import java.util.List;

public interface BankAccountService {
    Customer saveCustomer(Customer customer);
    Customer getCustomerByID(Long id) throws CustomerNotFoundException;
    CurrentAccount saveCurrentBankAccount(double initialBalance, double overDraft, Long customerID) throws CustomerNotFoundException;
    SavingAccount saveSavingBankAccount(double initialBalance, double interestRate, Long customerID) throws CustomerNotFoundException;
    List<Customer> listCustomers();
    BankAccount getBankAccountByID(String accountID) throws BankAccountNotFoundException;
    void debit(String accountID, double amount,String description) throws BankAccountNotFoundException, BalanceNotSufficientException;
    void credit(String accountID,double amount, String description) throws BankAccountNotFoundException;
    void transfer(String accountIdSource,String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException;
}
