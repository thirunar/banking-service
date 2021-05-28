package com.finmid.bankingservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finmid.bankingservice.exceptions.AccountNotFoundException;
import com.finmid.bankingservice.entity.Account;
import com.finmid.bankingservice.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Test
    void shouldCreateAnAccount() throws Exception {
        Account account = Account.builder().build();
        UUID id = UUID.randomUUID();
        when(accountService.createAccount(any())).thenReturn(account.toBuilder()
                .id(id).balance(new BigDecimal("100000.00"))
                .build());

        mockMvc.perform(post("/v1/account")
                .content(new ObjectMapper().writeValueAsString(account))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/v1/account/" + id));

        verify(accountService).createAccount(account);
    }

    @Test
    void shouldGetAnAccount() throws Exception {
        UUID id = UUID.randomUUID();
        when(accountService.getAccount(any())).thenReturn(Account.builder()
                .id(id).balance(new BigDecimal("100000.00"))
                .build());

        mockMvc.perform(get("/v1/account/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(id.toString())))
                .andExpect(jsonPath("$.balance", equalTo(100000.00)));

        verify(accountService).getAccount(id);
    }

    @Test
    void shouldReturnNotFoundWhenTheAccountNotExists() throws Exception {
        doThrow(AccountNotFoundException.class).when(accountService).getAccount(any());
        UUID id = UUID.randomUUID();

        mockMvc.perform(get("/v1/account/" + id))
                .andExpect(status().isNotFound());

        verify(accountService).getAccount(id);
    }
}