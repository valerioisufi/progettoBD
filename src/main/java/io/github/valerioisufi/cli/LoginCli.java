package io.github.valerioisufi.cli;

import io.github.valerioisufi.cli.io.InputReader;
import io.github.valerioisufi.cli.io.OutputPrinter;
import io.github.valerioisufi.exception.RequestException;
import io.github.valerioisufi.model.domain.Role;
import io.github.valerioisufi.model.dto.LoginDto;
import io.github.valerioisufi.utils.ValidationUtils;

import java.util.List;
import java.util.Optional;

public class LoginCli implements CliView {
    CliEngine engine;
    OutputPrinter printer;
    InputReader reader;

    @Override
    public CliView execute(CliEngine engine) {
        this.engine = engine;
        printer = engine.getPrinter();
        reader = engine.getInput();

        printer.printHeader("LOGIN");
        printer.printMenu(null, List.of("Accedi", "Esci"));

        int scelta = reader.readInt("Scegli un'opzione: ", 1, 2);

        if (scelta == 1) {
            return login();
        } else if (scelta == 2) {
            return null; // chiudi il programma
        }

        return this;
    }

    @Override
    public void stop() {

    }

    private CliView login() {
        String username = reader.readStringAndValidate("Username: ", input -> ValidationUtils.validateRequired(input, "username", 45));
        String password = reader.readStringAndValidate("Password: ", input -> ValidationUtils.validateRequired(input, "password", 32));

        LoginDto loginDto = new LoginDto(username, password);
        
        try {
            Optional<Role> role = engine.getControllerFactory().getLoginController().login(loginDto);
            if (role.isEmpty()) {
                printer.printError("Credenziali non valide");
                return this;
            }

            return switch (role.get()) {
                case Role.SEGRETERIA -> new SegreteriaCli();
                case Role.INSEGNANTE -> new InsegnanteCli();
            };

        } catch (RequestException e) {
            printer.printError("Login fallito: " + e.getMessage());
            return this; // Restiamo sulla schermata di login
        }

    }
}
