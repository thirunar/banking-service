package com.finmid.bankingservice.dto;

import com.finmid.bankingservice.entity.Transaction;
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
public class TransactionDto {

    private UUID txnId;
    private UUID fromAccountId;
    private UUID toAccountId;
    private BigDecimal amount;

    public static TransactionDto fromTransaction(Transaction transaction) {
        return TransactionDto.builder()
                .txnId(transaction.getTxnId())
                .fromAccountId(transaction.getFromAccount().getId())
                .toAccountId(transaction.getToAccount().getId())
                .amount(transaction.getAmount())
                .build();
    }
}
