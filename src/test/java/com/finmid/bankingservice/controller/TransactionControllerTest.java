package com.finmid.bankingservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finmid.bankingservice.dto.TransactionDto;
import com.finmid.bankingservice.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Test
    void shouldCreateATransaction() throws Exception {
        TransactionDto request = TransactionDto.builder().fromAccountId(UUID.randomUUID()).toAccountId(UUID.randomUUID()).amount(BigDecimal.TEN).build();
        UUID id = UUID.randomUUID();
        when(transactionService.transfer(any())).thenReturn(request.toBuilder().txnId(id).build());

        mockMvc.perform(post("/v1/transaction")
                .content(new ObjectMapper().writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/v1/transaction/" + id));

        verify(transactionService).transfer(request);
    }

    @Test
    void shouldReturnBadRequestWhenFromAndToAccountsAreSame() throws Exception {
        UUID id = UUID.randomUUID();
        TransactionDto request = TransactionDto.builder().fromAccountId(id).toAccountId(id).build();

        mockMvc.perform(post("/v1/transaction")
                .content(new ObjectMapper().writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(transactionService);
    }

}