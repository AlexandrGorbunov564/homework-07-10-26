package farit.dev.service.impl;

import farit.dev.service.AuthUserService;

public class AuthUserServiceImpl implements AuthUserService {

    @Override
    public boolean isValidUser(String username, String password) {
        return "user123".equals(username) && "password123".equals(password);
    }
}
