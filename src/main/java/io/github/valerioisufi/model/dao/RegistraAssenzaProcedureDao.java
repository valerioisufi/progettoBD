package io.github.valerioisufi.model.dao;

import io.github.valerioisufi.exception.DaoException;
import io.github.valerioisufi.model.domain.Assenza;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class RegistraAssenzaProcedureDao {

    public boolean registraAssenza(int idInsegnante, Assenza assenza) throws DaoException {
        try {
            Connection conn = DbConnection.getInstance().getConnection();
            CallableStatement cs = conn.prepareCall("{call registra_assenza(?,?,?,?)}");

            cs.setInt(1, idInsegnante);
            cs.setInt(2, assenza.getIdAllievo());
            cs.setInt(3, assenza.getCodiceLezione());
            cs.registerOutParameter(4, Types.TINYINT);

            cs.execute();

            boolean giaRegistrata = cs.getBoolean(4);
            return giaRegistrata;

        } catch (SQLException e) {
            throw new DaoException("Errore durante la registrazione dell'assenza: " + e.getMessage());
        }
    }
}
