package io.github.valerioisufi.model.dao;

import io.github.valerioisufi.exception.DaoException;
import io.github.valerioisufi.model.domain.Lezione;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ReportAgendaProcedureDao {

    public List<Lezione> reportAgendaSettimanale(int idInsegnante, LocalDate data) throws DaoException {
        List<Lezione> agenda = new ArrayList<>();

        try {
            Connection conn = DbConnection.getInstance().getConnection();
            CallableStatement cs = conn.prepareCall("{call report_agenda_settimanale(?, ?)}");

            cs.setInt(1, idInsegnante);
            cs.setObject(2, data);

            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                Lezione lezione = new Lezione(
                        -1, // codice non ritornato dalla select (solo se aggiunto nel DB)
                        rs.getDate("Data").toLocalDate(),
                        rs.getTime("OraInizio").toLocalTime(),
                        rs.getTime("OraFine").toLocalTime(),
                        idInsegnante,
                        rs.getString("NomeLivelloCorso"),
                        rs.getInt("CodiceCorso")
                );
                agenda.add(lezione);
            }

        } catch (SQLException e) {
            throw new DaoException("Errore durante il recupero dell'agenda settimanale: " + e.getMessage());
        }

        return agenda;
    }
}
