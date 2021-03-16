package com.trans.model;
import javax.persistence.*;

@Entity
@Table(name = "account")
public class Account {
    public Long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
    public String getName(){
        return this.user.getName();
    }
    public String getPin(){
        return this.user.getPin();
    }

    @Id
    private Long accountNumber;

    @OneToOne(targetEntity = User.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "id", referencedColumnName = "id",nullable = false)
    private User user;

    @Column
    private double amount;

    public Account() {
    }

    public Account(Long accountNumber, User user,double amount) {
        this.accountNumber = accountNumber;
        this.user = user;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "id  "+accountNumber + "name " + user.getName() + " amount - " + amount ;
    }
}
