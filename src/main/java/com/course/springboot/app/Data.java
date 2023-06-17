package com.course.springboot.app;

import com.course.springboot.app.models.Account;
import com.course.springboot.app.models.Bank;

import java.math.BigDecimal;

public class Data {
    public static final Account ACC_001 = new Account(1L, "Txema", new BigDecimal("1000"));
    public static final Account ACC_002 = new Account(1L, "Pepe", new BigDecimal("2000"));
    public static final Bank BANK = new Bank(1L, "Santander", 0);
}
