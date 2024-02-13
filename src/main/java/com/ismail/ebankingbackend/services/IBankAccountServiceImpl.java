package com.ismail.ebankingbackend.services;

import com.ismail.ebankingbackend.dtos.CustomerDTO;
import com.ismail.ebankingbackend.entities.*;

import com.ismail.ebankingbackend.enums.AccountStatus;
import com.ismail.ebankingbackend.enums.OperationType;
import com.ismail.ebankingbackend.exceptions.BalanceNotSufficientException;
import com.ismail.ebankingbackend.exceptions.BankAccountNotFoundException;
import com.ismail.ebankingbackend.exceptions.CustomerNotFoundException;
import com.ismail.ebankingbackend.mappers.BankAccountMapperImpl;
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
import java.util.stream.Collectors;


@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class IBankAccountServiceImpl implements IBankAccountService {

    private ICustomerRepository customerRepository;
    private IBankAccountRepository bankAccountRepository;
    private IAccountOperationRepository accountOperationRepository;

    private BankAccountMapperImpl dtoMapper;



    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        log.info("Saving new customer");
        Customer customer = dtoMapper.fromCustomerDto(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return dtoMapper.fromCustomer(savedCustomer);
    }

    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
        log.info("Updating a customer");
        Customer customer = dtoMapper.fromCustomerDto(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return dtoMapper.fromCustomer(savedCustomer);
    }

    @Override
    public void deleteCustomer(Long id)
    {
        customerRepository.deleteById(id);
    }

    @Override
    public CustomerDTO getCustomerByID(Long id) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(id).orElseThrow(()->new CustomerNotFoundException("Can not find customer"));
        return dtoMapper.fromCustomer(customer);
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

        Customer customer = customerRepository.findById(customerID).orElseThrow(()->new CustomerNotFoundException("Can not find customer"));
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

        Customer customer = customerRepository.findById(customerID).orElseThrow(()->new CustomerNotFoundException("Can not find customer"));
        if(customer==null){
            throw new CustomerNotFoundException("Customer not found");
        }else{
            bankAccount.setCustomer(customer);
        }
        return bankAccountRepository.save(bankAccount);
    }

    @Override
    public List<CustomerDTO> listCustomers() {
        List<Customer> customers = customerRepository.findAll();
        List<CustomerDTO> customerDTOS = customers.stream().map(customer -> {
           return  dtoMapper.fromCustomer(customer);
        }).collect(Collectors.toList());

        return customerDTOS;
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
