package com.finmid.bankingservice.dto;

import com.finmid.bankingservice.entity.Transaction;
import com.finmid.bankingservice.validation.ValidAccountIds;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ValidAccountIds
public class TransactionDto {

    private UUID txnId;
    @NotNull
    private UUID fromAccountId;
    @NotNull
    private UUID toAccountId;
    @NotNull
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
