package io.github.valerioisufi.cli.io;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.function.UnaryOperator;

public class InputReader {
    private final Scanner scanner;
    private final OutputPrinter printer;

    public InputReader(Scanner scanner, OutputPrinter printer) {
        this.scanner = scanner;
        this.printer = printer;
    }

    public String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public String readStringAndValidate(String prompt, UnaryOperator<String> validator) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        String msg = validator.apply(input);

        if (msg != null) {
            printer.printError(msg);
            return readStringAndValidate(prompt, validator);
        }
        return input;
    }

    public String readStringAndValidate(String prompt, UnaryOperator<String> validator, String defaultInput) {
        System.out.print(prompt + " [" + defaultInput + "]: ");
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            input = defaultInput;
        }

        String msg = validator.apply(input);

        if (msg != null) {
            printer.printError(msg);
            return readStringAndValidate(prompt, validator);
        }

        return input;
    }

    public int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                printer.printError("Input non valido. Inserisci un numero intero.");
            }
        }
    }

    public int readInt(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt);
            if (value >= min && value <= max) {
                return value;
            } else {
                printer.printError("Il valore deve essere compreso tra " + min + " e " + max + ".");
            }
        }
    }

    public double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            // Allow both comma and dot for decimals
            input = input.replace(",", ".");
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                printer.printError("Input non valido. Inserisci un numero decimale valido.");
            }
        }
    }

    public LocalDate readDate(String prompt, String pattern) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();

        try {
            return LocalDate.parse(input, DateTimeFormatter.ofPattern(pattern));

        } catch (DateTimeParseException e) {
            System.out.println("Formato non valido. Please use " + pattern + ".\n");
            return readDate(prompt, pattern);
        }

    }

    public LocalTime readTime(String prompt, String pattern) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();

        try {
            return LocalTime.parse(input, DateTimeFormatter.ofPattern(pattern));
        } catch (DateTimeParseException e) {
            System.out.println("Formato non valido. Please use " + pattern + ".\n");
            return readTime(prompt, pattern);
        }
    }

    public void waitForEnter() {
        System.out.println("Premi invio per continuare...");
        scanner.nextLine();
    }
}
