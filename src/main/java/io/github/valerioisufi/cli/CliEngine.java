package io.github.valerioisufi.cli;

import io.github.valerioisufi.cli.io.InputReader;
import io.github.valerioisufi.cli.io.OutputPrinter;
import io.github.valerioisufi.controller.AppControllerFactory;

import java.util.Scanner;

public class CliEngine {

    private CliView currentCliView;
    private final InputReader inputReader;
    private final OutputPrinter outputPrinter;

    private final AppControllerFactory controllerFactory;

    public CliEngine(AppControllerFactory controllerFactory) {
        this.currentCliView = new LoginCli();
        this.controllerFactory = controllerFactory;
        
        Scanner scanner = new Scanner(System.in);
        this.outputPrinter = new OutputPrinter();
        this.inputReader = new InputReader(scanner, outputPrinter);
    }

    public AppControllerFactory getControllerFactory() { return controllerFactory; }

    public InputReader getInput() { return inputReader; }
    public OutputPrinter getPrinter() { return outputPrinter; }

    public void start() {
        while (currentCliView != null) {

            CliView nextView = currentCliView.execute(this);


            if (currentCliView != nextView) {
                currentCliView.stop();
            }

            currentCliView = nextView;
        }
        outputPrinter.printInfo("Chiusura dell'applicazione.");
    }
}