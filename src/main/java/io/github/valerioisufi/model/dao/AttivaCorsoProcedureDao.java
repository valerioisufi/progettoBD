package io.github.valerioisufi.model.dao;

import io.github.valerioisufi.exception.DaoException;
import io.github.valerioisufi.model.domain.Corso;
import io.github.valerioisufi.model.domain.Lezione;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class AttivaCorsoProcedureDao {

    public void attivaCorso(Corso corso) throws DaoException {
        try {
            Connection conn = DbConnection.getInstance().getConnection();
            CallableStatement cs = conn.prepareCall("{call attiva_corso(?, ?)}");

            cs.setString(1, corso.getNomeLivello());

            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("[");
            
            if (corso.getLezioni() != null) {
                for (int i = 0; i < corso.getLezioni().size(); i++) {
                    Lezione l = corso.getLezioni().get(i);
                    jsonBuilder.append("{");
                    jsonBuilder.append("\"data\":\"").append(l.getData().toString()).append("\",");
                    jsonBuilder.append("\"oraInizio\":\"").append(l.getOraInizio().toString()).append("\",");
                    jsonBuilder.append("\"oraFine\":\"").append(l.getOraFine().toString()).append("\",");
                    jsonBuilder.append("\"idInsegnante\":").append(l.getIdInsegnante());
                    jsonBuilder.append("}");

                    if (i < corso.getLezioni().size() - 1) {
                        jsonBuilder.append(",");
                    }
                }
            }
            jsonBuilder.append("]");

            cs.setString(2, jsonBuilder.toString());

            cs.execute();

        } catch (SQLException e) {
            throw new DaoException("Errore durante l'attivazione del corso: " + e.getMessage());
        }
    }
}
