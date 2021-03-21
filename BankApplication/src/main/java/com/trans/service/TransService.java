package com.trans.service;

import com.trans.model.Account;
import com.trans.model.Transaction;
import com.trans.model.User;
import com.trans.repository.AccountRepository;
import com.trans.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class TransService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;
    public TransService(UserRepository userRepository, AccountRepository accountRepository){
        this.userRepository=userRepository;
        this.accountRepository=accountRepository;
    }
    public List<User> getUsers() {
        return (List<User>) userRepository.findAll();
    }

    public List<Account> getAccounts() {
        return (List<Account>) accountRepository.findAll();
    }

    public void getAccountsCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        String fileName="accounts.csv";
        String headerKey="Content-Disposition";
        String headerValue ="attachment;filename="+ fileName;

        response.setHeader(headerKey,headerValue);

        List<Account> accountList= (List<Account>) accountRepository.findAll();
        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

        String[] csvHeader = {"accountNumber", "name","pin","amount"};
        String[] nameMapping = {"accountNumber","name","pin","amount"};
        csvWriter.writeHeader(csvHeader);
        for (Account account : accountList){
            csvWriter.write(account,nameMapping);
        }
        csvWriter.close();
    }

    public String createAccount(Transaction transaction) {
        validateDuplicateAccount(transaction);
        User user = userRepository.save(new User(transaction.getAccountUserName(), transaction.getPin()));
        accountRepository.save(new Account(transaction.getAccountNumber(), user ,transaction.getAmount()));
        return "User Account is created with name " + transaction.getAccountUserName() + " and account Number " + transaction.getAccountNumber();
    }

    public String withdraw(Transaction transaction) throws RuntimeException {
        Account account = debitAccount(transaction);
        return "Amount " + transaction.getAmount() + " is debited from account " + account.getAccountNumber() + " balance is " + account.getAmount();
    }

    protected Account debitAccount(Transaction transaction) throws RuntimeException{
        Account account = validateAccount(transaction);
        if (account.getAmount() > transaction.getAmount()) {
            account.setAmount(account.getAmount() - transaction.getAmount());
            accountRepository.save(account);
        } else throw new RuntimeException("withdrawal amount is greater than balance in account");
        return account;
    }

    public String getBalance(Transaction transaction) throws RuntimeException {
        Account account = validateAccount(transaction);
        return "Balance in account " + transaction.getAccountNumber() + " is " + account.getAmount();
    }

    public String deposit(Transaction transaction) throws RuntimeException {
        Account account= creditAccount(transaction);
        return "Amount " + transaction.getAmount() + " is deposited in account " + account.getAccountNumber() + " balance is " + account.getAmount();
    }

    protected Account creditAccount(Transaction transaction) {
        Account account = validateAccount(transaction);
        if (transaction.getAmount() > 0) {
            account.setAmount(account.getAmount() + transaction.getAmount());
            accountRepository.save(account);
        } else throw new RuntimeException("Invalid Amount");
        return account;
    }

    public String initialise(Transaction transaction) throws RuntimeException {
        Account account = creditInitialAmount(transaction);
        return "Amount " + transaction.getAmount() + " is deposited in account " + account.getAccountNumber() + " balance is " + account.getAmount();
    }

    protected Account creditInitialAmount(Transaction transaction) {
        Account account = validateAccount(transaction);
        if (transaction.getAmount() > 5000) {
            account.setAmount(transaction.getAmount());
            accountRepository.save(account);
        } else throw new RuntimeException("Minimum amount to be added is 5000");
        return account;
    }

    protected Account validateAccount(Transaction transaction) throws RuntimeException {
        Optional<Account> accountOptional = accountRepository.findById(transaction.getAccountNumber());
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            if (account.getUser().getName().equalsIgnoreCase(transaction.getAccountUserName())
                    && account.getUser().getPin().equalsIgnoreCase(transaction.getPin())) {
                return account;
            } else throw new RuntimeException("Invalid user");
        } else throw new RuntimeException("Account does not exist");
    }

    protected void validateDuplicateAccount(Transaction transaction) throws RuntimeException{
        Optional<Account> accountOptional = accountRepository.findById(transaction.getAccountNumber());
        if (accountOptional.isPresent()){
            throw new RuntimeException(("Account already exists"));
        }
    }
}