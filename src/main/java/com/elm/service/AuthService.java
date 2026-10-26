package com.elm.service;

import com.elm.dao.UserDAO;
import com.elm.model.User;
import com.elm.util.PasswordUtil;

/**
 * Service handling authentication operations.
 */
public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    public String getLastError() {
        return userDAO.getLastError();
    }

    /**
     * Authenticates user by username and plain-text password using SHA-256.
     *
     * @param username       Candidate username
     * @param plainPassword  Plain-text password to hash and verify
     * @return User object on success, or null on failure
     */
    public User login(String username, String plainPassword) {
        if (username == null || username.trim().isEmpty() || 
            plainPassword == null || plainPassword.trim().isEmpty()) {
            return null;
        }

        String hashedPassword = PasswordUtil.hashPassword(plainPassword.trim());
        return userDAO.authenticate(username.trim(), hashedPassword);
    }
}
