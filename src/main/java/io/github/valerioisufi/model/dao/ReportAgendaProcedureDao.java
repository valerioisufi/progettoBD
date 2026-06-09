package io.github.valerioisufi.model.dao;

import io.github.valerioisufi.exception.DaoException;
import io.github.valerioisufi.model.domain.Lezione;

import java.sql.*;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;

public class ReportAgendaProcedureDao {

    public List<Lezione> reportAgendaSettimanale(int idInsegnante, LocalDate data) throws DaoException {
        List<Lezione> agenda = new ArrayList<>();

        try {
            Connection conn = DbConnection.getInstance().getConnection();
            CallableStatement cs = conn.prepareCall("{call report_agenda_settimanale(?, ?)}");

            cs.setInt(1, idInsegnante);
            cs.setDate(2, Date.valueOf(data));

            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                Lezione lezione = new Lezione(
                        rs.getInt("Codice"),
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
