package com.GoldenOpportunity;

public interface User {
    String name = null;
    String password = null;
    //TODO: Must add this to the DCD/Domain Model
    String contactInfo = null;

    void login(String username, String password);
    void authenticateUser();
    void updateProfile(UpdatedInfo updatedInfo);

    class UpdatedInfo {
        String newUsername;
        String newPassword;
    }
}
