package com.ismail.ebankingbackend.dtos;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class CustomerDTO {
    private Long id;
    private String name;
    private String email;
}
