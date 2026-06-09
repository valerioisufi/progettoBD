package io.github.valerioisufi.controller;

import io.github.valerioisufi.exception.DaoException;
import io.github.valerioisufi.exception.RequestException;
import io.github.valerioisufi.model.dao.LoginProcedureDao;
import io.github.valerioisufi.model.domain.Credentials;
import io.github.valerioisufi.model.domain.Role;
import io.github.valerioisufi.model.dto.LoginDto;

import java.util.Optional;

public class LoginController {

    private final SessionManager sessionManager;

    public LoginController(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public Optional<Role> login(LoginDto loginDto) throws RequestException {
        Credentials cred = new Credentials(loginDto.username(), loginDto.password(), null, -1);
        LoginProcedureDao loginProcedureDao = new LoginProcedureDao();

        try {
            Credentials credentials = loginProcedureDao.execute(cred);
            if (credentials.getRole() == null) {
                return Optional.empty();
            }
            sessionManager.loginUser(credentials);
            
            return Optional.of(credentials.getRole());

        } catch (DaoException ex) {
            throw new RequestException(ex.getMessage());
        }
    }
}
