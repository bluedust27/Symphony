package com.trans.controller;

import com.trans.model.Account;
import com.trans.model.Transaction;
import com.trans.model.User;
import com.trans.service.TransService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TransController {
    @Autowired
    private TransService transService;

    @GetMapping("/users")
    public List<User> getUsers() {
        return transService.getUsers();
    }

    @GetMapping("/accounts")
    public List<Account> getAccounts() {
        return transService.getAccounts();
    }

    @PostMapping("/create")
    public String createAccount(@RequestBody Transaction transaction) {
        return transService.createAccount(transaction);
    }

    @PostMapping("/init")
    public String addInitialBalance(@RequestBody Transaction transaction) {
        String message = "";
        try {
            message = transService.initiallise(transaction);
        } catch (RuntimeException ex) {
            message = ex.getMessage();
        }
        return message;
    }

    @PostMapping("/withdraw")
    public String withdraw(@RequestBody Transaction transaction) {
        String message = "";
        try {
            message = transService.withdraw(transaction);
        } catch (RuntimeException ex) {
            message = ex.getMessage();
        }
        return message;
    }

    @PostMapping("/deposit")
    public String deposit(@RequestBody Transaction transaction) {
        String message = "";
        try {
           message = transService.deposit(transaction);
        } catch (RuntimeException ex) {
            message = ex.getMessage();
        }
        return message;
    }

    @PostMapping("/balance")
    public String balance(@RequestBody Transaction transaction) {
        String message = "";
        try {
            message = transService.getBalance(transaction);
        } catch (RuntimeException ex) {
            message = ex.getMessage();
        }
        return message;
    }
}