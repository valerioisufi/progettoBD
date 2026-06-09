package io.github.valerioisufi.model.dao;

import io.github.valerioisufi.exception.DaoException;
import io.github.valerioisufi.model.domain.Allievo;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class IscriviAllievoProcedureDao {

    public void iscriviAllievo(Allievo allievo) throws DaoException {
        try {
            Connection conn = DbConnection.getInstance().getConnection();
            CallableStatement cs = conn.prepareCall("{call iscrivi_allievo(?, ?, ?, ?, ?, ?)}");

            cs.setString(1, allievo.getNome());
            cs.setString(2, allievo.getCognome());
            
            if (allievo.getTelefono() != null) {
                cs.setString(3, allievo.getTelefono());
            } else {
                cs.setNull(3, Types.VARCHAR);
            }
            
            if (allievo.getEmail() != null) {
                cs.setString(4, allievo.getEmail());
            } else {
                cs.setNull(4, Types.VARCHAR);
            }
            
            cs.setString(5, allievo.getNomeLivelloCorso());
            cs.setInt(6, allievo.getCodiceCorso());

            cs.execute();

        } catch (SQLException e) {
            throw new DaoException("Errore durante l'iscrizione dell'allievo: " + e.getMessage());
        }
    }
}
