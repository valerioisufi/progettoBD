package io.github.valerioisufi.controller;

import io.github.valerioisufi.model.dao.DbConnection;
import io.github.valerioisufi.model.domain.Credentials;
import io.github.valerioisufi.model.domain.Role;

import java.util.Optional;

public class SessionManager {
    private Credentials currentUser;

    public void loginUser(Credentials credentials) {
        this.currentUser = credentials;

        String dbRole = mapRoleToDbUser(credentials.getRole());

        DbConnection.getInstance().changeUser(dbRole);
    }

    public void logout() {
        this.currentUser = null;
        DbConnection.getInstance().changeUser("login");
    }

    private String mapRoleToDbUser(Role role) {
        return switch (role) {
            case INSEGNANTE -> "insegnante";
            case SEGRETERIA -> "segreteria";
        };
    }

    public Optional<String> getUsername() {
        return Optional.ofNullable(currentUser).map(Credentials::getUsername);
    }

    public Optional<Integer> getIdInsegnanteCorrente() {
        return Optional.ofNullable(currentUser).map(Credentials::getIdInsegnante);
    }
}
