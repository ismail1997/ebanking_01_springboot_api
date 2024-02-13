package com.ismail.ebankingbackend.forms;


import lombok.*;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@ToString
public class DebitForm {
    private String accountID;
    private double amount;
    private String description;
}
