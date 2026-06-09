package io.github.valerioisufi.model.dao;

import io.github.valerioisufi.exception.DaoException;
import io.github.valerioisufi.model.domain.Insegnante;
import io.github.valerioisufi.model.domain.Lezione;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReportLezioniSvolteProcedureDao {

    public List<Lezione> reportLezioniSvolte(int mese, int anno) throws DaoException {
        List<Lezione> report = new ArrayList<>();

        try {
            Connection conn = DbConnection.getInstance().getConnection();
            CallableStatement cs = conn.prepareCall("{call report_lezioni_svolte(?, ?)}");

            cs.setInt(1, mese);
            cs.setInt(2, anno);

            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                Lezione lezione = new Lezione(
                        rs.getInt("Codice"),
                        rs.getDate("Data").toLocalDate(),
                        rs.getTime("OraInizio").toLocalTime(),
                        rs.getTime("OraFine").toLocalTime(),
                        rs.getInt("IdInsegnante"),
                        rs.getString("NomeLivelloCorso"),
                        rs.getInt("CodiceCorso")
                );

                Insegnante insegnante = new Insegnante(
                        rs.getInt("IdInsegnante"),
                        rs.getString("Nome"),
                        rs.getString("Cognome"),
                        null, null, null, null, null // I campi non ritornati li lasciamo a null
                );
                
                lezione.setInsegnante(insegnante);
                report.add(lezione);
            }

        } catch (SQLException e) {
            throw new DaoException("Errore durante la generazione del report: " + e.getMessage());
        }

        return report;
    }
}
