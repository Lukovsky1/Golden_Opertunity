package com.GoldenOpportunity.Roles;

import com.GoldenOpportunity.User;
import com.GoldenOpportunity.Login.enums.Role;

public class Admin extends User {

    public Admin(int id, String username, String password, String contactInfo) {
        super(id, username, password, contactInfo, Role.ADMIN);
    }
}