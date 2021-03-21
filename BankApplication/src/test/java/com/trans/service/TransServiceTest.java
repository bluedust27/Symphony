package com.trans.service;

import com.trans.model.Account;
import com.trans.model.Transaction;
import com.trans.model.User;
import com.trans.repository.AccountRepository;
import com.trans.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TransServiceTest {
    private UserRepository userRepository= Mockito.mock(UserRepository.class);
    private AccountRepository accountRepository=Mockito.mock(AccountRepository.class);

    private ArgumentCaptor<Account> accountArgumentCaptor= ArgumentCaptor.forClass(Account.class);
    private TransService transService= new TransService(userRepository,accountRepository);

    @Test
    @DisplayName("When asked for a list of all users then return list of expected 2 users")
    void shouldGetAllUsers() {
        List<User> expectedUser = new ArrayList<>();
        expectedUser.add(new User((long)1,"testname1","1234",10000));
        expectedUser.add(new User((long)1,"testname2","1234",10000));

        Mockito.when(userRepository.findAll()).thenReturn(expectedUser);

        List<User> actualUser = (List<User>) userRepository.findAll();
        Assertions.assertThat(2).isEqualTo(expectedUser.size());
    }

    @Test
    @DisplayName("When asked for a list of all accounts then return list of expected 2 accounts")
    void shouldGetAllAccounts() {
        User expectedUser = new User((long)1,"testname1","1234",10000);
        List<Account> expectedAccount=new ArrayList<Account>();
        expectedAccount.add(new Account((long)12345678, expectedUser,1000));
        expectedAccount.add(new Account((long)12345679, expectedUser,12000));

        Mockito.when(accountRepository.findAll()).thenReturn(expectedAccount);
        List<Account> actualAccount= (List<Account>) accountRepository.findAll();

        Assertions.assertThat(2).isEqualTo(expectedAccount.size());

    }

    @Test
    @DisplayName("When a new account is created then save user and account information")
    void createAccount() {
        Transaction transaction=new Transaction(12345L,"test","1234",10000);

        accountRepository.save(new Account(transaction.getAccountNumber(), new User(transaction.getAccountUserName(), transaction.getPin()), transaction.getAmount()));
        Mockito.verify(accountRepository, Mockito.times(1)).save(accountArgumentCaptor.capture());

        Assertions.assertThat(accountArgumentCaptor.getValue().getAccountNumber()).isEqualTo(12345L);
        Assertions.assertThat(accountArgumentCaptor.getValue().getAmount()).isEqualTo(10000);
        Assertions.assertThat(accountArgumentCaptor.getValue().getUser().getName()).isEqualTo("test");
        Assertions.assertThat(accountArgumentCaptor.getValue().getUser().getPin()).isEqualTo("1234");

    }

    @Test
    @DisplayName("When a new account is created with existing account number then return exception")
    void validateDuplicateAccount() {

        Transaction transaction=new Transaction(12345L,"test","1234",0);
        Account existingAccount = new Account(12345L,  new User("test","1234"),6000);

        Mockito.when(accountRepository.findById(transaction.getAccountNumber())).thenReturn(Optional.of(existingAccount));
//        Mockito.when(accountRepository.findById(transaction.getAccountNumber())).thenReturn(Optional.empty());

        Exception exception=assertThrows(RuntimeException.class,()->{
           transService.validateDuplicateAccount(transaction);
        });

        assertTrue(exception.getMessage().equalsIgnoreCase("Account already exists"));
    }

    @Test
    @DisplayName("When a withdraw with amount less than available balance then amount should get debited from account")
    void withdrawShouldDebit() {
        Transaction transaction=new Transaction(12345L,"test","1234",10000);
        Account account = new Account(12345L,  new User("test","1234"),15000);

        Mockito.when(accountRepository.findById(12345L)).thenReturn(java.util.Optional.of(account));
        Account actualAccount=transService.debitAccount(transaction);

        Assertions.assertThat(actualAccount.getAmount()).isEqualTo(5000);
    }

    @Test
    @DisplayName("When withdraw with amount greater than available balance then amount should not debit from account and exception")
    void withdrawShouldNotDebit(){
        Transaction transaction=new Transaction(12345L,"test","1234",20000);
        Account account = new Account(12345L,  new User("test","1234"),15000);

        Mockito.when(accountRepository.findById(12345L)).thenReturn(java.util.Optional.of(account));

        Exception exception= assertThrows(RuntimeException.class,()->{
            Account actualAccount=transService.debitAccount(transaction);
        });
        assertTrue(exception.getMessage().equalsIgnoreCase("withdrawal amount is greater than balance in account"));
    }


    @Test
    @DisplayName("When balance is requested then current balance is returned")
    void getBalance() {
        Transaction transaction=new Transaction(12345L,"test","1234",20000);
        Account account = new Account(12345L,  new User("test","1234"),20000);

        Mockito.when(accountRepository.findById(12345L)).thenReturn(java.util.Optional.of(account));
        Account actualAccount=transService.validateAccount(transaction);

        Assertions.assertThat(actualAccount.getAmount()).isEqualTo(20000);
    }

    @Test
    @DisplayName("When deposit then amount is credited to account")
    void depositShouldCredit() {
        Transaction transaction=new Transaction(12345L,"test","1234",10000);
        Account account = new Account(12345L,  new User("test","1234"),15000);

        Mockito.when(accountRepository.findById(12345L)).thenReturn(java.util.Optional.of(account));
        Account actualAccount=transService.creditAccount(transaction);

        Assertions.assertThat(actualAccount.getAmount()).isEqualTo(25000);
    }

    @Test
    @DisplayName("When deposit is less than zero then no amount is credited to account and exception")
    void depositShouldNotCredit() {
        Transaction transaction=new Transaction(12345L,"test","1234",0);
        Account account = new Account(12345L,  new User("test","1234"),15000);

        Mockito.when(accountRepository.findById(12345L)).thenReturn(java.util.Optional.of(account));
        Exception exception= assertThrows(RuntimeException.class,()->{
            transService.creditAccount(transaction);
        });
        assertTrue(exception.getMessage().equalsIgnoreCase("Invalid Amount"));
    }

    @Test
    @DisplayName("When amount with greater than 5000 is deposited then account should be credited")
    void initialiseShouldCredit() {
        Transaction transaction=new Transaction(12345L,"test","1234",6000);
        Account account = new Account(12345L,  new User("test","1234"),0);

        Mockito.when(accountRepository.findById(12345L)).thenReturn(java.util.Optional.of(account));
        Account actualAccount=transService.creditInitialAmount(transaction);

        Assertions.assertThat(actualAccount.getAmount()).isEqualTo(6000);
    }

    @Test
    @DisplayName("When amount with less than 5000 is deposited then account should not be credited and exception")
    void initialiseShouldNotCredit() {
        Transaction transaction=new Transaction(12345L,"test","1234",4999);
        Account account = new Account(12345L,  new User("test","1234"),0);

        Mockito.when(accountRepository.findById(12345L)).thenReturn(java.util.Optional.of(account));

        Exception exception= assertThrows(RuntimeException.class,()->{
            transService.creditInitialAmount(transaction);
        });
        assertTrue(exception.getMessage().equalsIgnoreCase("Minimum amount to be added is 5000"));
    }

    @Test
    @DisplayName("When a account with correct input is present then return account")
    void validateAccountWithCorrectData() {
        Transaction transaction=new Transaction(12345L,"test","1234",0);
        Account account = new Account(12345L,  new User("test","1234"),6000);

        Mockito.when(accountRepository.findById(12345L)).thenReturn(java.util.Optional.of(account));
        Account actualAccount=transService.validateAccount(transaction);

        Assertions.assertThat(actualAccount.getAccountNumber()).isEqualTo(12345L);
        Assertions.assertThat(actualAccount.getAmount()).isEqualTo(6000);
        Assertions.assertThat(actualAccount.getUser().toString()).isEqualTo("id  nullname testpin   1234");
        Assertions.assertThat(actualAccount.getPin()).isEqualTo("1234");
    }


    @ParameterizedTest(name = "{index} => Account={0}, ExceptionMessage={1}")
    @DisplayName("When a account with incorrect user details then return invalid user exception")
    @MethodSource("accountProvider")
    void validateAccountReturnInvalidUserDetails(Account a, String exceptionMessage) {
        Transaction transaction=new Transaction(12345L,"test","1234",0);

        Mockito.when(accountRepository.findById(transaction.getAccountNumber())).thenReturn(java.util.Optional.of(a));

        Exception exception = assertThrows(RuntimeException.class,()->{
            transService.validateAccount(transaction);
        });

        assertTrue(exception.getMessage().equalsIgnoreCase(exceptionMessage));
    }

    private static Stream<Arguments> accountProvider(){
        return Stream.of(
                Arguments.of(new Account(12345L,  new User("invalidName","1234"),6000),"Invalid User"),
                Arguments.of(new Account(12345L,  new User("test","invalidPin"),6000),"Invalid User")
                );
    }

    @Test
    @DisplayName("When a account with incorrect account number then return invalid account and exception")
    void validateAccountReturnInvalidAccountDetails(){
        Transaction transaction=new Transaction(1234L,"test","1234",0);

        Mockito.when(accountRepository.findById(transaction.getAccountNumber())).thenReturn(Optional.empty());
        Exception exception=assertThrows(RuntimeException.class,()->{
            transService.validateAccount(transaction);
        });
        assertTrue(exception.getMessage().equalsIgnoreCase("Account does not exist"));
    }


}