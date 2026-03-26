package com.GoldenOpportunity.Roles;

import com.GoldenOpportunity.User;
import com.GoldenOpportunity.Login.enums.Role;

public class Clerk extends User {

    public Clerk(int id, String username, String password, String contactInfo) {
        super(id, username, password, contactInfo, Role.CLERK);
    }
}