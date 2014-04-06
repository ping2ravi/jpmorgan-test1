package com.jpmorgan.gce.interview.batch;

public class DownloadRequest {

    private final AccountID accountID;
    private final GroupID groupID;

    DownloadRequest(AccountID accountID, GroupID groupID) {
        this.accountID = accountID;
        this.groupID = groupID;
    }

    public AccountID getAccountID() {
        return accountID;
    }

    public GroupID getGroupID() {
        return groupID;
    }
}
