package io.github.valerioisufi.model.domain;

import java.time.LocalDate;
import java.time.LocalTime;

public class Lezione {
    private int codice;
    private LocalDate data;
    private LocalTime oraInizio;
    private LocalTime oraFine;
    private int idInsegnante;
    private String nomeLivelloCorso;
    private int codiceCorso;

    // Opzionale per i report
    private Insegnante insegnante;

    public Lezione(int codice, LocalDate data, LocalTime oraInizio, LocalTime oraFine, int idInsegnante, String nomeLivelloCorso, int codiceCorso) {
        this.codice = codice;
        this.data = data;
        this.oraInizio = oraInizio;
        this.oraFine = oraFine;
        this.idInsegnante = idInsegnante;
        this.nomeLivelloCorso = nomeLivelloCorso;
        this.codiceCorso = codiceCorso;
    }

    public Lezione(LocalDate data, LocalTime oraInizio, LocalTime oraFine, int idInsegnante) {
        this.data = data;
        this.oraInizio = oraInizio;
        this.oraFine = oraFine;
        this.idInsegnante = idInsegnante;
    }

    public int getCodice() { return codice; }
    public LocalDate getData() { return data; }
    public LocalTime getOraInizio() { return oraInizio; }
    public LocalTime getOraFine() { return oraFine; }
    public int getIdInsegnante() { return idInsegnante; }
    public String getNomeLivelloCorso() { return nomeLivelloCorso; }
    public int getCodiceCorso() { return codiceCorso; }
    
    public Insegnante getInsegnante() { return insegnante; }
    public void setInsegnante(Insegnante insegnante) { this.insegnante = insegnante; }
}
