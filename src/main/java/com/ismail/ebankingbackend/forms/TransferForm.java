package com.ismail.ebankingbackend.forms;


import lombok.*;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@ToString
public class TransferForm {
    private String accountIdSource;
    private String accountIdDestination;
    private double amount;
    private String description;
}
