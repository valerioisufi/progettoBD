package io.github.valerioisufi.controller;

public class AppControllerFactory {

    SessionManager sessionManager;

    public AppControllerFactory(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public LoginController getLoginController() {
        return new LoginController(sessionManager);
    }

    public SegreteriaController getSegreteriaController() {
        return new SegreteriaController();
    }

    public InsegnanteController getInsegnanteController() {
        return new InsegnanteController(sessionManager);
    }
}
