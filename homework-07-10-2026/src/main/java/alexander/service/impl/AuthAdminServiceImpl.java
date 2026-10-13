package alexander.service.impl;

import alexander.service.AuthAdminService;

public class AuthAdminServiceImpl implements AuthAdminService {

    @Override
    public boolean isValidAdmin(String login, String password) {
        return "admin123".equals(login) && "ytrewq".equals(password);
    }
}