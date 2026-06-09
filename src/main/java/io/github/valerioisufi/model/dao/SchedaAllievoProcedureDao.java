package io.github.valerioisufi.model.dao;

import io.github.valerioisufi.exception.DaoException;
import io.github.valerioisufi.model.domain.Allievo;
import io.github.valerioisufi.model.domain.Lezione;
import io.github.valerioisufi.model.domain.SchedaAllievo;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SchedaAllievoProcedureDao {

    public SchedaAllievo consultaSchedaAllievo(int idAllievo) throws DaoException {
        Allievo allievo = null;
        List<Lezione> assenze = new ArrayList<>();

        try {
            Connection conn = DbConnection.getInstance().getConnection();
            CallableStatement cs = conn.prepareCall("{call consulta_scheda_allievo(?)}");

            cs.setInt(1, idAllievo);

            boolean hasResults = cs.execute();

            if (hasResults) {
                ResultSet rsAllievo = cs.getResultSet();
                if (rsAllievo.next()) {
                    allievo = new Allievo(
                            rsAllievo.getInt("Id"),
                            rsAllievo.getString("Nome"),
                            rsAllievo.getString("Cognome"),
                            rsAllievo.getString("Telefono"),
                            rsAllievo.getString("Email"),
                            rsAllievo.getString("NomeLivello"),
                            rsAllievo.getInt("Codice"),
                            rsAllievo.getDate("DataIscrizione").toLocalDate()
                    );
                }

                if (cs.getMoreResults()) {
                    ResultSet rsAssenze = cs.getResultSet();
                    while (rsAssenze.next()) {
                        Lezione assenza = new Lezione(
                                rsAssenze.getInt("Codice"),
                                rsAssenze.getDate("Data").toLocalDate(),
                                rsAssenze.getTime("OraInizio").toLocalTime(),
                                rsAssenze.getTime("OraFine").toLocalTime(),
                                -1, 
                                allievo != null ? allievo.getNomeLivelloCorso() : "",
                                allievo != null ? allievo.getCodiceCorso() : -1
                        );
                        assenze.add(assenza);
                    }
                }
            }

            if (allievo == null) {
                throw new DaoException("Allievo con ID " + idAllievo + " non trovato.");
            }

        } catch (SQLException e) {
            throw new DaoException("Errore durante la consultazione della scheda allievo: " + e.getMessage());
        }

        return new SchedaAllievo(allievo, assenze);
    }
}
