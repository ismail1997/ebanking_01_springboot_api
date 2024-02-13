package com.ismail.ebankingbackend.web;


import com.ismail.ebankingbackend.dtos.AccountHistoryDTO;
import com.ismail.ebankingbackend.dtos.AccountOperationDTO;
import com.ismail.ebankingbackend.dtos.BankAccountDTO;
import com.ismail.ebankingbackend.entities.AccountOperation;
import com.ismail.ebankingbackend.entities.BankAccount;
import com.ismail.ebankingbackend.exceptions.BalanceNotSufficientException;
import com.ismail.ebankingbackend.exceptions.BankAccountNotFoundException;
import com.ismail.ebankingbackend.forms.CreditForm;
import com.ismail.ebankingbackend.forms.DebitForm;
import com.ismail.ebankingbackend.forms.TransferForm;
import com.ismail.ebankingbackend.services.IBankAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class BankAccountRestController {

    private IBankAccountService bankAccountService;

    public BankAccountRestController(IBankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<BankAccountDTO> getBankAccount(@PathVariable("id") String accountID) throws BankAccountNotFoundException {
        BankAccountDTO account = this.bankAccountService.getBankAccountByID(accountID);
        return ResponseEntity.ok().body(account);
    }

    @GetMapping
    public ResponseEntity<List<BankAccountDTO>> getAllBankAccounts()
    {
        return ResponseEntity.ok().body(this.bankAccountService.listBankAccounts());
    }

    @GetMapping("/{id}/operations")
    public ResponseEntity<List<AccountOperationDTO>> getAllAccountOperations(@PathVariable("id") String id){
        return ResponseEntity.ok().body(this.bankAccountService.accountHistory(id));
    }
    @GetMapping("/{id}/pageOperations")
    public ResponseEntity<AccountHistoryDTO> getAllAccountHistory(
            @PathVariable("id") String id,
            @RequestParam(name = "page",defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size) throws BankAccountNotFoundException
    {
        return ResponseEntity.ok().body(this.bankAccountService.getAccountHistory(id,page , size));
    }


    @PostMapping("/debit")
    public void debit(@RequestBody DebitForm debitForm) throws BankAccountNotFoundException, BalanceNotSufficientException {
        bankAccountService.debit(debitForm.getAccountID(), debitForm.getAmount(), debitForm.getDescription());
    }


    @PostMapping("/credit")
    public void credit(@RequestBody CreditForm creditForm) throws BankAccountNotFoundException {
        bankAccountService.credit(creditForm.getAccountID(), creditForm.getAmount(), creditForm.getDescription());
    }

    @PostMapping("/transfer")
    public void transfer(@RequestBody TransferForm transferForm) throws BankAccountNotFoundException, BalanceNotSufficientException {
        bankAccountService.transfer(transferForm.getAccountIdSource(),transferForm.getAccountIdDestination(),transferForm.getAmount());
    }



}
