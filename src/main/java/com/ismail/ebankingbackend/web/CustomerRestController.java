package com.ismail.ebankingbackend.web;

import com.ismail.ebankingbackend.dtos.BankAccountDTO;
import com.ismail.ebankingbackend.dtos.CustomerDTO;
import com.ismail.ebankingbackend.entities.Customer;
import com.ismail.ebankingbackend.exceptions.CustomerNotFoundException;
import com.ismail.ebankingbackend.services.IBankAccountService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/customers")
@Slf4j
@AllArgsConstructor
public class CustomerRestController {
    private IBankAccountService bankAccountService;


    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getAll(){
        return ResponseEntity.ok().body(this.bankAccountService.listCustomers());
    }

    @GetMapping("/search")
    public ResponseEntity<List<CustomerDTO>> getAll(@RequestParam(name = "keyword",defaultValue = "") String keyword){
        return ResponseEntity.ok().body(this.bankAccountService.searchCustomers("%"+keyword+"%"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable("id") Long customerID) throws CustomerNotFoundException {
        return ResponseEntity.ok().body(this.bankAccountService.getCustomerByID(customerID));
    }

    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(@RequestBody CustomerDTO customerDTO)
    {
        return ResponseEntity.created(URI.create("/customers/customerID")).body(bankAccountService.saveCustomer(customerDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable("id") Long id, @RequestBody CustomerDTO customerDTO)
    {
        customerDTO.setId(id);
        return ResponseEntity.ok().body(bankAccountService.updateCustomer(customerDTO));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable("id")  Long id)
    {
        this.bankAccountService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/accounts")
    public ResponseEntity<List<BankAccountDTO>>getAccountsOfCustomer(@PathVariable("id") Long id){
        return ResponseEntity.ok().body(this.bankAccountService.listBankAccountOfCustomer(id));
    }

}
