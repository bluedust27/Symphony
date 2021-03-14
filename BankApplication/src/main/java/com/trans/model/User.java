package com.trans.model;

import org.springframework.stereotype.Component;

import javax.persistence.*;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String name;

    @Column
    private String pin;

    public User() {
    }

    public User(Long id, String name, String pin,double amount) {
        this.id = id;
        this.name = name;
        this.pin = pin;
    }

    public User(String name, String pin) {
        this.name = name;
        this.pin = pin;
    }

//    @OneToOne(fetch = FetchType.LAZY,
//            cascade =  CascadeType.ALL,
//            mappedBy = "user")
//    private Account account;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    @Override
    public String toString() {
        return "id  "+id + "name " + name +"pin   "+pin;
    }
}
