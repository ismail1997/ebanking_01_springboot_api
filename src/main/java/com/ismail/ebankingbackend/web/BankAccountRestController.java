package com.ismail.ebankingbackend.web;


import com.ismail.ebankingbackend.services.IBankAccountService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bank")
public class BankAccountRestController {

    private IBankAccountService bankAccountService;

    public BankAccountRestController(IBankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }


}
