package io.github.valerioisufi;

import io.github.valerioisufi.cli.CliEngine;
import io.github.valerioisufi.controller.AppControllerFactory;
import io.github.valerioisufi.controller.SessionManager;

public class Main {
    public static void main(String[] args) {
        SessionManager sessionManager = new SessionManager();
        AppControllerFactory factory = new AppControllerFactory(sessionManager);

        CliEngine cliEngine = new CliEngine(factory);
        cliEngine.start();
    }
}