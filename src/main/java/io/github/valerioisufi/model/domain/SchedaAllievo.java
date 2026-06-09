package io.github.valerioisufi.model.domain;

import java.util.List;

public class SchedaAllievo {
    private Allievo allievo;
    private List<Lezione> assenze;

    public SchedaAllievo(Allievo allievo, List<Lezione> assenze) {
        this.allievo = allievo;
        this.assenze = assenze;
    }

    public Allievo getAllievo() {
        return allievo;
    }

    public List<Lezione> getAssenze() {
        return assenze;
    }
}
