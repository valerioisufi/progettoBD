package io.github.valerioisufi.model.domain;

import java.time.LocalDate;
import java.util.List;

public class Corso {
    private String nomeLivello;
    private int codice;
    private LocalDate dataAttivazione;
    private int numAllievi;

    // Campi derivati dal livello
    private String libro;
    private boolean esameObbligatorio;

    private List<Lezione> lezioni;

    public Corso(String nomeLivello, int codice, LocalDate dataAttivazione, int numAllievi, String libro, boolean esameObbligatorio) {
        this.nomeLivello = nomeLivello;
        this.codice = codice;
        this.dataAttivazione = dataAttivazione;
        this.numAllievi = numAllievi;
        this.libro = libro;
        this.esameObbligatorio = esameObbligatorio;
    }

    public Corso(String nomeLivello, int codice, List<Lezione> lezioni) {
        this.nomeLivello = nomeLivello;
        this.codice = codice;
        this.lezioni = lezioni;
    }

    public String getNomeLivello() { return nomeLivello; }
    public int getCodice() { return codice; }
    public LocalDate getDataAttivazione() { return dataAttivazione; }
    public int getNumAllievi() { return numAllievi; }
    public String getLibro() { return libro; }
    public boolean isEsameObbligatorio() { return esameObbligatorio; }
    public List<Lezione> getLezioni() { return lezioni; }
    public void setLezioni(List<Lezione> lezioni) { this.lezioni = lezioni; }
}
