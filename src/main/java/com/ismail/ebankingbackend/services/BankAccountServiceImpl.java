package com.ismail.ebankingbackend.services;

import com.ismail.ebankingbackend.dtos.*;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
public class BankAccountServiceImpl implements IBankAccountService {

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
    public CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerID) throws CustomerNotFoundException {
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

        return dtoMapper.fromCurrentAccount(bankAccountRepository.save(bankAccount));
    }

    @Override
    public SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerID) throws CustomerNotFoundException {
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
        return dtoMapper.fromSavingBankAccount( bankAccountRepository.save(bankAccount));
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
    public BankAccountDTO getBankAccountByID(String accountID) throws BankAccountNotFoundException{
        BankAccount bankAccount = bankAccountRepository.findById(accountID)
                .orElseThrow(()->new BankAccountNotFoundException("Could not find any bank account with such id"));
        if(bankAccount instanceof SavingAccount){
            SavingAccount savingAccount = (SavingAccount) bankAccount;
            return dtoMapper.fromSavingBankAccount(savingAccount);
        }else{
            CurrentAccount currentAccount = (CurrentAccount) bankAccount;
            return dtoMapper.fromCurrentAccount(currentAccount);
        }

    }

    @Override
    public List<BankAccountDTO> listBankAccounts() {
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();

        List<BankAccountDTO> bankAccountDTOS = bankAccounts.stream().map(bankAccount -> {
            if (bankAccount instanceof CurrentAccount) {
                return dtoMapper.fromCurrentAccount((CurrentAccount) bankAccount);
            } else {
                return dtoMapper.fromSavingBankAccount((SavingAccount) bankAccount);
            }
        }).collect(Collectors.toList());

        return bankAccountDTOS;
    }

    @Override
    public void debit(String accountID, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException {
        BankAccount bankAccount = bankAccountRepository.findById(accountID)
                .orElseThrow(()->new BankAccountNotFoundException("Could not find any bank account with such id"));
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
        BankAccount bankAccount = bankAccountRepository.findById(accountID)
                .orElseThrow(()->new BankAccountNotFoundException("Could not find any bank account with such id"));

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


    @Override
    public List<AccountOperationDTO> accountHistory(String accountID){
        List<AccountOperation> accountOperations = accountOperationRepository.findByBankAccountId(accountID);
        return accountOperations.stream().map(accountOperation -> {
            return   dtoMapper.fromAccountOperation(accountOperation);
        }).collect(Collectors.toList());
    }

    @Override
    public AccountHistoryDTO getAccountHistory(String id, int page, int size) throws BankAccountNotFoundException {

        BankAccount bankAccount = bankAccountRepository.findById(id).orElse(null);

        if(bankAccount==null){
            throw new BankAccountNotFoundException("Can not find any account ");
        }

        Page<AccountOperation> accountOperations = accountOperationRepository.findByBankAccountId(id, PageRequest.of(page, size));
        AccountHistoryDTO accountHistoryDTO = new AccountHistoryDTO();

        List<AccountOperationDTO> accountOperationDTOS = accountOperations.getContent().stream().map(accountOperation -> {
            return dtoMapper.fromAccountOperation(accountOperation);
        }).collect(Collectors.toList());

        accountHistoryDTO.setAccountOperationDTOS(accountOperationDTOS);
        accountHistoryDTO.setAccountId(bankAccount.getId());
        accountHistoryDTO.setBalance(bankAccount.getBalance());
        accountHistoryDTO.setCurrentPage(page);
        accountHistoryDTO.setPageSize(size);
        accountHistoryDTO.setTotalPages(accountOperations.getTotalPages());


        return accountHistoryDTO;
    }


    @Override
    public List<CustomerDTO> searchCustomers(String keyword)
    {
        List<CustomerDTO> customerDTOS = customerRepository.searchCustomers(keyword)
                .stream()
                .map(customer -> dtoMapper.fromCustomer(customer))
                .collect(Collectors.toList());

        return customerDTOS;
    }

    @Override
    public List<BankAccountDTO> listBankAccountOfCustomer(Long id) {
        List<BankAccountDTO> bankAccountDTOList = this.bankAccountRepository.getBankAccountsByCustomer(id).stream().map(bankAccount -> {
            if (bankAccount instanceof CurrentAccount) {
                return dtoMapper.fromCurrentAccount((CurrentAccount) bankAccount);
            } else {
                return dtoMapper.fromSavingBankAccount((SavingAccount) bankAccount);
            }
        }).collect(Collectors.toList());
        return bankAccountDTOList;
    }


}
