package io.github.valerioisufi.controller;

import io.github.valerioisufi.exception.DaoException;
import io.github.valerioisufi.exception.RequestException;
import io.github.valerioisufi.model.dao.ElencoIscrittiCorsoProcedureDao;
import io.github.valerioisufi.model.dao.RegistraAssenzaProcedureDao;
import io.github.valerioisufi.model.dao.ReportAgendaProcedureDao;
import io.github.valerioisufi.model.domain.Allievo;
import io.github.valerioisufi.model.domain.Assenza;
import io.github.valerioisufi.model.domain.Corso;
import io.github.valerioisufi.model.domain.Lezione;
import io.github.valerioisufi.model.dto.AllievoDto;
import io.github.valerioisufi.model.dto.LezioneDto;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class InsegnanteController {

    private final SessionManager sessionManager;

    public InsegnanteController(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public List<LezioneDto> reportAgendaSettimanale(LocalDate dataInizio) throws RequestException {
        ReportAgendaProcedureDao dao = new ReportAgendaProcedureDao();
        
        int idInsegnante = sessionManager.getIdInsegnanteCorrente()
                .orElseThrow(() -> new RequestException("Impossibile recuperare l'ID insegnante dalla sessione."));
                
        try {
            List<Lezione> agenda = dao.reportAgendaSettimanale(idInsegnante, dataInizio);
            
            return agenda.stream()
                    .map(l -> new LezioneDto(
                            l.getCodice(), l.getData(), l.getOraInizio(), l.getOraFine(),
                            l.getIdInsegnante(), l.getNomeLivelloCorso(), l.getCodiceCorso(), null
                    ))
                    .toList();
                    
        } catch (DaoException e) {
            throw new RequestException(e.getMessage());
        }
    }

    public List<AllievoDto> elencoIscrittiCorso(String nomeLivello, int codiceCorso) throws RequestException {
        Corso corso = new Corso(nomeLivello, codiceCorso, null, 0, null, false);
        ElencoIscrittiCorsoProcedureDao dao = new ElencoIscrittiCorsoProcedureDao();
        
        try {
            List<Allievo> iscritti = dao.elencoIscrittiCorso(corso);
            
            return iscritti.stream()
                    .map(a -> new AllievoDto(
                            a.getId(), a.getNome(), a.getCognome(), a.getTelefono(),
                            a.getEmail(), a.getNomeLivelloCorso(), a.getCodiceCorso(), a.getDataIscrizione()
                    ))
                    .toList();
                    
        } catch (DaoException e) {
            throw new RequestException(e.getMessage());
        }
    }

    public void registraAssenza(int idAllievo, int codiceLezione) throws RequestException {
        Assenza assenza = new Assenza(idAllievo, codiceLezione);
        RegistraAssenzaProcedureDao dao = new RegistraAssenzaProcedureDao();
        
        try {
            dao.registraAssenza(assenza);
        } catch (DaoException e) {
            throw new RequestException(e.getMessage());
        }
    }
}
