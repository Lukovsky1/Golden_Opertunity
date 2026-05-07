package com.GoldenOpportunity;

import com.GoldenOpportunity.DatabaseTools.DBUtil;
import com.GoldenOpportunity.Login.enums.*;
import com.GoldenOpportunity.Roles.Clerk;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class User {
    private final int id;
    private String username;
    private String password;
    private String contactInfo;
    private final Role role;
    private AccountStatus accountStatus;
    private int failedLoginCount;

    public User(int id, String username, String password, String contactInfo, Role role){
        this.id = id;
        this.username = username;
        this.password = password;
        this.contactInfo = contactInfo;
        this.role = role;
        this.accountStatus = AccountStatus.ACTIVE;
        this.failedLoginCount = 0;
    }

    public int getUserId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public Role getRole() {
        return role;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public int getFailedLoginCount() {
        return failedLoginCount;
    }

    public boolean isActive() {
        return accountStatus == AccountStatus.ACTIVE;
    }

    public void incrementFailedLoginCount() {
        failedLoginCount++;
    }

    public void resetFailedLoginCount() {
        failedLoginCount = 0;
    }

    public void lockAccount() {
        accountStatus = AccountStatus.LOCKED;
    }

    public void disableAccount() {
        accountStatus = AccountStatus.DISABLED;
    }
}
