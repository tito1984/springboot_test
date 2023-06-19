package com.course.springboot.app.models;

import java.math.BigDecimal;

public class TransactionDTO {
    private Long accountOriginId;
    private Long accountDestinyId;
    private BigDecimal amount;
    private Long bankId;

    public Long getAccountOriginId() {
        return accountOriginId;
    }

    public void setAccountOriginId(Long accountOriginId) {
        this.accountOriginId = accountOriginId;
    }

    public Long getAccountDestinyId() {
        return accountDestinyId;
    }

    public void setAccountDestinyId(Long accountDestinyId) {
        this.accountDestinyId = accountDestinyId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getBankId() {
        return bankId;
    }

    public void setBankId(Long bankId) {
        this.bankId = bankId;
    }
}
