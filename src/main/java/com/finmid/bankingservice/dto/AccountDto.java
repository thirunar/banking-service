package com.finmid.bankingservice.dto;

import com.finmid.bankingservice.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {

    private UUID id;

    private BigDecimal balance = new BigDecimal("100000.00");

    public static AccountDto fromAccount(Account account) {
        return AccountDto.builder()
                .id(account.getId())
                .balance(account.getBalance())
                .build();
    }
}
