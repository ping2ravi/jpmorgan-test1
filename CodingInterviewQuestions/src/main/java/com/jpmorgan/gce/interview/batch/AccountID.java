package com.jpmorgan.gce.interview.batch;

public class AccountID {

    private final String accountCode;

    AccountID(String accountCode) {
        this.accountCode = accountCode;
    }

    public String getAccountCode() {
        return accountCode;
    }
}
