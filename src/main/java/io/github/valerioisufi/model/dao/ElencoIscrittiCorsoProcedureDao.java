package io.github.valerioisufi.model.dao;

import io.github.valerioisufi.exception.DaoException;
import io.github.valerioisufi.model.domain.Allievo;
import io.github.valerioisufi.model.domain.Corso;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ElencoIscrittiCorsoProcedureDao {

    public List<Allievo> elencoIscrittiCorso(Corso corso) throws DaoException {
        List<Allievo> iscritti = new ArrayList<>();

        try {
            Connection conn = DbConnection.getInstance().getConnection();
            CallableStatement cs = conn.prepareCall("{call elenco_iscritti_corso(?, ?)}");

            cs.setString(1, corso.getNomeLivello());
            cs.setInt(2, corso.getCodice());

            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                Allievo allievo = new Allievo(
                        rs.getInt("Id"),
                        rs.getString("Nome"),
                        rs.getString("Cognome"),
                        rs.getString("Telefono"),
                        rs.getString("Email"),
                        corso.getNomeLivello(),
                        corso.getCodice(),
                        rs.getDate("DataIscrizione").toLocalDate()
                );
                iscritti.add(allievo);
            }

        } catch (SQLException e) {
            throw new DaoException("Errore durante il recupero dell'elenco iscritti: " + e.getMessage());
        }

        return iscritti;
    }
}
