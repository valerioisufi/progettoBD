package io.github.valerioisufi.cli;

import io.github.valerioisufi.cli.io.InputReader;
import io.github.valerioisufi.cli.io.OutputPrinter;
import io.github.valerioisufi.controller.InsegnanteController;
import io.github.valerioisufi.exception.RequestException;
import io.github.valerioisufi.model.dto.AllievoDto;
import io.github.valerioisufi.model.dto.LezioneDto;
import io.github.valerioisufi.utils.ValidationUtils;

import java.time.LocalDate;
import java.util.List;

public class InsegnanteCli implements CliView {
    private CliEngine engine;
    private OutputPrinter printer;
    private InputReader reader;

    private InsegnanteController controller;

    @Override
    public CliView execute(CliEngine engine) {
        this.engine = engine;
        printer = engine.getPrinter();
        reader = engine.getInput();

        controller = engine.getControllerFactory().getInsegnanteController();

        printer.printHeader("DASHBOARD Insegnante");
        printer.printMenu(null, List.of("Appello", "Report agenda settimanale", "Elenco iscritti corso", "Logout"));

        int scelta = reader.readInt("Scegli un'opzione: ", 1, 4);

        if (scelta == 1) {
            appello();
        } else if (scelta == 2) {
            reportAgenda();
        } else if (scelta == 3) {
            elencoIscrittiCorso();
        } else if (scelta == 4) {
            engine.getControllerFactory().getSessionManager().logout();
            return new LoginCli();
        }

        return this;
    }

    @Override
    public void stop() {}

    private void appello() {
        try {
            List<LezioneDto> lezioni = controller.reportAgendaSettimanale(LocalDate.now());

            if (lezioni.isEmpty()) {
                printer.printInfo("Non ci sono lezioni in agenda questa settimana.");
                return;
            }

            List<String> data = lezioni.stream()
                    .map(l ->
                            l.data() + " " + l.oraInizio() + "-" + l.oraFine() + l.nomeLivelloCorso() + " " + l.codiceCorso()
                    ).toList();

            printer.printMenu("Lezioni programmate", data);

            int sceltaLezione = reader.readInt("Scegli la lezione per la quale vuoi fare l'appello: ", 1, lezioni.size());

            LezioneDto lezione = lezioni.get(sceltaLezione - 1);

            List<AllievoDto> iscritti = controller.elencoIscrittiCorso(lezione.nomeLivelloCorso(), lezione.codiceCorso());
            if (iscritti.isEmpty()) {
                printer.printInfo("Non ci sono iscritti per questa lezione.");
                return;
            }

            List<String> iscrittiData = iscritti.stream()
                    .map(a ->
                            a.nome() + " " + a.cognome()
                    ).toList();

            while (true) {
                printer.printMenu("Iscritti", iscrittiData);

                int sceltaAllievo = reader.readInt("Di chi vuoi registrare l'assenza? (0 per terminare): ", 0, iscrittiData.size());
                if (sceltaAllievo == 0) {
                    break;
                }

                AllievoDto allievo = iscritti.get(sceltaAllievo - 1);

                try {
                    controller.registraAssenza(allievo.id(), lezione.codice());

                    printer.printSuccess("Assenza registrata per " + allievo.nome() + " " + allievo.cognome());
                    reader.waitForEnter();
                } catch (RequestException e){
                    printer.printError("Errore durante la registrazione dell'assenza: " + e.getMessage());
                }


            }

        } catch (RequestException e){
            printer.printError("Errore durante l'appello: " + e.getMessage());
        }
    }

    private void reportAgenda() {
        LocalDate dataInizio = reader.readDate("Data inizio settimana (yyyy-MM-dd): ", "yyyy-MM-dd");

        try {
            List<LezioneDto> lezioni = controller.reportAgendaSettimanale(dataInizio);

            String[] headers = {"Codice", "Data", "Ora inizio", "Ora fine", "Corso"};
            String[][] data = lezioni.stream()
                    .map(l -> new String[]{
                            String.valueOf(l.codice()),
                            l.data().toString(),
                            l.oraInizio().toString(),
                            l.oraFine().toString(),
                            l.nomeLivelloCorso() + " " + l.codiceCorso()
                    })
                    .toArray(String[][]::new);

            printer.printTable(headers, data);
            reader.waitForEnter();

        } catch (RequestException e){
            printer.printError("Errore durante il recupero dell'agenda settimanale: " + e.getMessage());
        }
    }

    private void elencoIscrittiCorso() {
        String nomeLivello = reader.readStringAndValidate("Nome livello corso: ",
                input -> ValidationUtils.validateRequired(input, "nome livello", 45));
        int codiceCorso = reader.readInt("Codice corso: ");

        try {
            List<AllievoDto> iscritti = controller.elencoIscrittiCorso(nomeLivello, codiceCorso);

            String[] headers = {"ID", "Nome", "Cognome", "Telefono", "Email"};
            String[][] data = iscritti.stream()
                    .map(a -> new String[]{
                            String.valueOf(a.id()),
                            a.nome(),
                            a.cognome(),
                            a.telefono(),
                            a.email()
                    })
                    .toArray(String[][]::new);

            printer.printTable(headers, data);
            reader.waitForEnter();

        } catch (RequestException e){
            printer.printError("Errore durante il recupero dell'elenco degli iscritti al corso: " + e.getMessage());
        }

    }

}
