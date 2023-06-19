package com.course.springboot.app.controllers;

import com.course.springboot.app.models.Account;
import com.course.springboot.app.models.TransactionDTO;
import com.course.springboot.app.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Account findById(@PathVariable Long id) {
        return accountService.findById(id);
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransactionDTO transactionDTO) {
        accountService.transfer(transactionDTO.getAccountOriginId(),
                transactionDTO.getAccountDestinyId(),
                transactionDTO.getAmount(), transactionDTO.getBankId());
        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("message", "Transfer done");
        response.put("transaction", transactionDTO);
        return ResponseEntity.ok(response);
    }


}
