package io.github.valerioisufi.model.domain;

public class Assenza {
    private int idAllievo;
    private int codiceLezione;

    public Assenza(int idAllievo, int codiceLezione) {
        this.idAllievo = idAllievo;
        this.codiceLezione = codiceLezione;
    }

    public int getIdAllievo() { return idAllievo; }
    public int getCodiceLezione() { return codiceLezione; }
}
