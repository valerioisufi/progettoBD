package io.github.valerioisufi.controller;

import io.github.valerioisufi.exception.DaoException;
import io.github.valerioisufi.exception.RequestException;
import io.github.valerioisufi.model.dao.*;
import io.github.valerioisufi.model.domain.Allievo;
import io.github.valerioisufi.model.domain.Corso;
import io.github.valerioisufi.model.domain.Lezione;
import io.github.valerioisufi.model.domain.SchedaAllievo;
import io.github.valerioisufi.model.dto.*;

import java.util.List;

public class SegreteriaController {

    public void attivaCorso(AttivaCorsoDto dto) throws RequestException {
        List<Lezione> lezioni = null;
        
        if (dto.lezioni() != null) {
            lezioni = dto.lezioni().stream()
                    .map(l -> new Lezione(l.data(), l.oraInizio(), l.oraFine(), l.idInsegnante()))
                    .toList();
        }

        Corso corso = new Corso(dto.nomeLivello(), -1, lezioni);
        AttivaCorsoProcedureDao dao = new AttivaCorsoProcedureDao();
        
        try {
            dao.attivaCorso(corso);
        } catch (DaoException e) {
            throw new RequestException(e.getMessage());
        }
    }

    public void iscriviAllievo(IscriviAllievoDto dto) throws RequestException {
        Allievo allievo = new Allievo(
                dto.nome(), dto.cognome(), dto.telefono(), dto.email(), dto.nomeLivelloCorso(), dto.codiceCorso()
        );
        IscriviAllievoProcedureDao dao = new IscriviAllievoProcedureDao();
        
        try {
            dao.iscriviAllievo(allievo);
        } catch (DaoException e) {
            throw new RequestException(e.getMessage());
        }
    }

    public List<LezioneDto> reportLezioniSvolte(int mese, int anno) throws RequestException {
        ReportLezioniSvolteProcedureDao dao = new ReportLezioniSvolteProcedureDao();
        
        try {
            List<Lezione> lezioni = dao.reportLezioniSvolte(mese, anno);

            return lezioni.stream().map(l -> {
                InsegnanteDto insegnanteDto = null;
                if (l.getInsegnante() != null) {
                    insegnanteDto = new InsegnanteDto(
                            l.getInsegnante().getId(),
                            l.getInsegnante().getNome(),
                            l.getInsegnante().getCognome(),
                            null, null, null, null, null
                    );
                }

                return new LezioneDto(
                        l.getCodice(), l.getData(), l.getOraInizio(), l.getOraFine(),
                        l.getIdInsegnante(), l.getNomeLivelloCorso(), l.getCodiceCorso(), insegnanteDto
                );
            }).toList();

        } catch (DaoException e) {
            throw new RequestException(e.getMessage());
        }
    }

    public List<CorsoDto> listaCorsi() throws RequestException {
        ListaCorsiProcedureDao dao = new ListaCorsiProcedureDao();
        
        try {
            List<Corso> corsi = dao.listaCorsi();
            return corsi.stream()
                    .map(c -> new CorsoDto(
                            c.getNomeLivello(), c.getCodice(), c.getDataAttivazione(),
                            c.getNumAllievi(), c.getLibro(), c.isEsameObbligatorio()
                    ))
                    .toList();

        } catch (DaoException e) {
            throw new RequestException(e.getMessage());
        }
    }

    public SchedaAllievoDto consultaSchedaAllievo(int idAllievo) throws RequestException {
        SchedaAllievoProcedureDao dao = new SchedaAllievoProcedureDao();
        
        try {
            SchedaAllievo schedaAllievo = dao.consultaSchedaAllievo(idAllievo);

            Allievo a = schedaAllievo.getAllievo();
            AllievoDto allievoDto = new AllievoDto(
                    a.getId(), a.getNome(), a.getCognome(),
                    a.getTelefono(), a.getEmail(), a.getNomeLivelloCorso(), a.getCodiceCorso(), a.getDataIscrizione()
            );
            List<LezioneDto> assenze = schedaAllievo.getAssenze().stream()
                    .map(l -> new LezioneDto(
                            l.getCodice(), l.getData(), l.getOraInizio(), l.getOraFine(),
                            -1, l.getNomeLivelloCorso(), l.getCodiceCorso(), null
                    )).toList();

            return new SchedaAllievoDto(allievoDto,  assenze);

        } catch (DaoException e) {
            throw new RequestException(e.getMessage());
        }
    }
}
