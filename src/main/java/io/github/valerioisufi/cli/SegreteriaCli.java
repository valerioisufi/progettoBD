package io.github.valerioisufi.cli;

import io.github.valerioisufi.cli.io.InputReader;
import io.github.valerioisufi.cli.io.OutputPrinter;
import io.github.valerioisufi.controller.SegreteriaController;
import io.github.valerioisufi.exception.RequestException;
import io.github.valerioisufi.model.dto.*;
import io.github.valerioisufi.utils.ValidationUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class SegreteriaCli implements CliView {
    private CliEngine engine;
    private OutputPrinter printer;
    private InputReader reader;

    SegreteriaController controller;

    @Override
    public CliView execute(CliEngine engine) {
        this.engine = engine;
        printer = engine.getPrinter();
        reader = engine.getInput();

        controller = engine.getControllerFactory().getSegreteriaController();

        printer.printHeader("DASHBOARD segreteria");
        printer.printMenu(null, List.of("Attiva corso", "Iscrivi allievo", "Report lezioni svolte", "Lista corsi", "Consulta scheda allievo", "Logout"));

        int scelta = reader.readInt("Scegli un'opzione: ", 1, 6);

        if (scelta == 1) {
            attivaCorso();
        } else if (scelta == 2) {
            iscriviAllievo();
        } else if (scelta == 3) {
            reportLezioniSvolte();
        } else if (scelta == 4) {
            listaCorsi();
        } else if (scelta == 5) {
            schedaAllievo();
        } else if (scelta == 6) {
            engine.getControllerFactory().getSessionManager().logout();
            return new LoginCli();
        }

        return this;
    }

    @Override
    public void stop() {}

    private void attivaCorso() {
        String nomeLivello = reader.readStringAndValidate("Nome livello corso: ",
                input -> ValidationUtils.validateRequired(input, "nome livello", 45));

        List<LezioneDto> lezioni = new ArrayList<>();

        boolean done = false;
        while (!done) {
            printer.printTitle("Informazioni lezione (" + (lezioni.size() + 1) + ")");
            LocalDate dataLezione = reader.readDate("Data (yyyy-MM-dd): ", "yyyy-MM-dd");
            LocalTime oraInizio = reader.readTime("Ora Inizio (HH:mm): ", "HH:mm");
            LocalTime oraFine = reader.readTime("Ora Fine (HH:mm): ", "HH:mm");

            int idInsegnante = reader.readInt("ID Insegnante per questa lezione: ");

            LezioneDto lezione = new LezioneDto(-1, dataLezione, oraInizio, oraFine, idInsegnante, nomeLivello, -1, null);
            lezioni.add(lezione);

            done = !reader.readString("Vuoi inserire un'altra lezione? (s/n): ").equalsIgnoreCase("s");

        }

        AttivaCorsoDto corso = new AttivaCorsoDto(nomeLivello, lezioni);

        try {
            controller.attivaCorso(corso);

            printer.printSuccess("Corso attivato con successo");
            reader.waitForEnter();
        } catch (RequestException e) {
            printer.printError("Errore durante la creazione del corso: " + e.getMessage());
            modificaInformazioniCorso(corso);
        }


    }

    private void modificaInformazioniCorso(AttivaCorsoDto corso) {
        printer.printMenu("Cosa vuoi fare?", List.of("Modifica livello corso", "Modifica lezione", "Salva e riprova", "Annulla"));

        int scelta = reader.readInt("Scegli un'opzione: ", 1, 4);

        if (scelta == 1) {
            String nomeLivello = reader.readStringAndValidate("Nome livello corso: ",
                    input -> ValidationUtils.validateRequired(input, "nome livello", 45));

            AttivaCorsoDto newCorso = new AttivaCorsoDto(nomeLivello, corso.lezioni());
            modificaInformazioniCorso(newCorso);
            
        } else if (scelta == 2) {
            printer.printMenu("Quale lezione vuoi modificare?", corso.lezioni().stream()
                    .map(l -> "Lezione del " + l.data() + " (" + l.oraInizio() + " - " + l.oraFine() + ")")
                    .toList()
            );

            int sceltaLezione = reader.readInt("Scegli un'opzione: ", 1, corso.lezioni().size());
            LezioneDto lezione = corso.lezioni().get(sceltaLezione - 1);

            LocalDate dataLezione = reader.readDate("Data (yyyy-MM-dd): ", "yyyy-MM-dd");
            LocalTime oraInizio = reader.readTime("Ora Inizio (HH:mm): ", "HH:mm");
            LocalTime oraFine = reader.readTime("Ora Fine (HH:mm): ", "HH:mm");

            int idInsegnante = reader.readInt("ID Insegnante per questa lezione: ");

            List<LezioneDto> newLezioni = new ArrayList<>(corso.lezioni());
            newLezioni.set(sceltaLezione - 1, new LezioneDto(-1, dataLezione, oraInizio, oraFine, idInsegnante, corso.nomeLivello(), -1, null));
            AttivaCorsoDto newCorso = new AttivaCorsoDto(corso.nomeLivello(), newLezioni);

            modificaInformazioniCorso(newCorso);

        } else if (scelta == 3) {
            try {
                controller.attivaCorso(corso);

                printer.printSuccess("Corso attivato con successo");
                reader.waitForEnter();
            } catch (RequestException e) {
                printer.printError("Errore durante la creazione del corso: " + e.getMessage());
                modificaInformazioniCorso(corso);
            }

        } else if (scelta == 4) {
            return;
        }

    }

    private void iscriviAllievo() {
        String nome = reader.readStringAndValidate("Nome: ", input -> ValidationUtils.validateRequired(input, "nome", 45));
        String cognome = reader.readStringAndValidate("Cognome: ", input -> ValidationUtils.validateRequired(input, "cognome", 45));
        String telefonoInput = reader.readStringAndValidate("Telefono: ", ValidationUtils::validatePhone);
        String telefono = telefonoInput.isEmpty() ? null : telefonoInput;

        String emailInput = reader.readStringAndValidate("Email: ", ValidationUtils::validateEmail);
        String email = emailInput.isEmpty() ? null : emailInput;

        String nomeLivelloCorso = reader.readStringAndValidate("Nome livello corso: ", input -> ValidationUtils.validateRequired(input, "nome", 45));
        int codiceCorso = reader.readInt("Codice corso: ");

        IscriviAllievoDto iscriviAllievoDto = new IscriviAllievoDto(nome, cognome, telefono, email, nomeLivelloCorso, codiceCorso);

        try {
            controller.iscriviAllievo(iscriviAllievoDto);
            printer.printSuccess("Allievo iscritto con successo");

        } catch (RequestException e) {
            printer.printError("Iscrizione dell'allievo fallita: " + e.getMessage());
        }

        reader.waitForEnter();
    }

    private void reportLezioniSvolte() {
        YearMonth date = reader.readYearMonth("Data (yyyy-MM): ", "yyyy-MM");
        int month = date.getMonthValue();
        int year = date.getYear();

        try {
            List<LezioneDto> lezioni = controller.reportLezioniSvolte(month, year);

            if (lezioni.isEmpty()) {
                printer.printInfo("Nessuna lezione trovata per il mese indicato.");

            } else {
                String[] headers = {"Codice", "Data", "Ora inizio", "Ora fine", "Insegnante", "Corso"};
                String[][] data = lezioni.stream()
                        .map(l -> itemLezione(l, true))
                        .toArray(String[][]::new);

                printer.printTable(headers, data);
            }

        } catch (RequestException e) {
            printer.printError("Generazione del report fallita: " + e.getMessage());
        }

        reader.waitForEnter();

    }

    private void listaCorsi() {
        try {
            List<CorsoDto> corsi = controller.listaCorsi();

            if(corsi.isEmpty()) {
                printer.printInfo("Nessun corso attualmente attivo.");

            } else {
                String[] headers = {"Nome livello", "Codice", "Data di attivazione", "Num. allievi", "Libro di testo", "Esame obbligatorio"};
                String[][] data = corsi.stream()
                        .map(c -> new String[]{
                                c.nomeLivello(),
                                String.valueOf(c.codice()),
                                c.dataAttivazione().toString(),
                                String.valueOf(c.numAllievi()),
                                c.libro(),
                                c.esameObbligatorio() ? "Sì" : "No"
                        }).toArray(String[][]::new);

                printer.printTable(headers, data);
            }

        } catch (RequestException e) {
            printer.printError("Errore durante il recupero della lista dei corsi: " + e.getMessage());
        }

        reader.waitForEnter();

    }

    private void schedaAllievo() {
        int idAllievo = reader.readInt("ID allievo: ");

        try {
            SchedaAllievoDto schedaAllievo = controller.consultaSchedaAllievo(idAllievo);
            AllievoDto a = schedaAllievo.allievo();

            printer.printTitle("Scheda allievo: " + a.nome() + " " + a.cognome());
            String[][] dataAllievo = {
                    {"ID Allievo", String.valueOf(a.id())},
                    {"Telefono", (a.telefono() != null ? a.telefono() : "N/D")},
                    {"Email", (a.email() != null ? a.email() : "N/D")},
                    {"Corso", a.nomeLivelloCorso() + " " + a.codiceCorso()},
                    {"Iscritto il", a.dataIscrizione().toString()}
            };
            printer.printTable(new String[]{"Campo", "Valore"}, dataAllievo);

            printer.printTitle("Assenze registrate (" + schedaAllievo.assenze().size() + ")");
            if (schedaAllievo.assenze().isEmpty()) {
                printer.printInfo("Questo allievo non ha assenze registrate.");

            } else {
                String[] headers = {"Codice Lez.", "Data", "Ora inizio", "Ora fine", "Corso"};

                String[][] data = schedaAllievo.assenze().stream()
                        .map(l -> itemLezione(l, false))
                        .toArray(String[][]::new);

                printer.printTable(headers, data);
            }

        } catch (RequestException e) {
            printer.printError("Errore durante il recupero della scheda dell'allievo: " + e.getMessage());
        }

        reader.waitForEnter();
    }

    private String[] itemLezione(LezioneDto l, boolean printInsegnante) {

        String corso = l.nomeLivelloCorso() + " " + l.codiceCorso();

        if (printInsegnante) {
            String insegnante = l.insegnante() != null ? "(" + l.insegnante().id() + ")" + l.insegnante().nome() + " " + l.insegnante().cognome() : "N/A";

            return new String[]{
                    String.valueOf(l.codice()),
                    l.data().toString(),
                    l.oraInizio().toString(),
                    l.oraFine().toString(),
                    insegnante,
                    corso
            };

        } else {
            return new String[]{
                    String.valueOf(l.codice()),
                    l.data().toString(),
                    l.oraInizio().toString(),
                    l.oraFine().toString(),
                    corso
            };
        }

    }

}
