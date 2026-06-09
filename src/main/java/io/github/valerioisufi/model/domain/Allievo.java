package io.github.valerioisufi.model.domain;

import java.time.LocalDate;

public class Allievo {
    private int id;
    private String nome;
    private String cognome;
    private String telefono;
    private String email;
    private String nomeLivelloCorso;
    private int codiceCorso;
    private LocalDate dataIscrizione;

    public Allievo(int id, String nome, String cognome, String telefono, String email, String nomeLivelloCorso, int codiceCorso, LocalDate dataIscrizione) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.telefono = telefono;
        this.email = email;
        this.nomeLivelloCorso = nomeLivelloCorso;
        this.codiceCorso = codiceCorso;
        this.dataIscrizione = dataIscrizione;
    }

    public Allievo(String nome, String cognome, String telefono, String email, String nomeLivelloCorso, int codiceCorso) {
        this.nome = nome;
        this.cognome = cognome;
        this.telefono = telefono;
        this.email = email;
        this.nomeLivelloCorso = nomeLivelloCorso;
        this.codiceCorso = codiceCorso;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getCognome() { return cognome; }
    public String getTelefono() { return telefono; }
    public String getEmail() { return email; }
    public String getNomeLivelloCorso() { return nomeLivelloCorso; }
    public int getCodiceCorso() { return codiceCorso; }
    public LocalDate getDataIscrizione() { return dataIscrizione; }
}
