package io.github.valerioisufi.cli;

import io.github.valerioisufi.cli.io.InputReader;
import io.github.valerioisufi.cli.io.OutputPrinter;
import io.github.valerioisufi.utils.ValidationUtils;

import java.util.List;

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
        String username = reader.readString("Username: ");

        return null;
    }
}
