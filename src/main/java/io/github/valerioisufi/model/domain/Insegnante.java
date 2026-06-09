package io.github.valerioisufi.model.domain;


public class Insegnante {
    private int id;
    private String nome;
    private String cognome;
    private String nazioneProvenienza;
    private String via;
    private String numeroCivico;
    private String cap;
    private String citta;

    public Insegnante(int id, String nome, String cognome, String nazioneProvenienza, String via, String numeroCivico, String cap, String citta) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.nazioneProvenienza = nazioneProvenienza;
        this.via = via;
        this.numeroCivico = numeroCivico;
        this.cap = cap;
        this.citta = citta;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getCognome() { return cognome; }
    public String getNazioneProvenienza() { return nazioneProvenienza; }
    public String getVia() { return via; }
    public String getNumeroCivico() { return numeroCivico; }
    public String getCap() { return cap; }
    public String getCitta() { return citta; }
}
