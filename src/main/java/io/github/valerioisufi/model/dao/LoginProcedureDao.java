package io.github.valerioisufi.model.dao;

import io.github.valerioisufi.exception.DaoException;
import io.github.valerioisufi.model.domain.Credentials;
import io.github.valerioisufi.model.domain.Role;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class LoginProcedureDao {

    public Credentials execute(Credentials input) throws DaoException {
        int role;
        int idInsegnante;

        try {
            Connection conn = DbConnection.getInstance().getConnection();
            CallableStatement cs = conn.prepareCall("{call login(?,?,?,?)}");
            cs.setString(1, input.getUsername());
            cs.setString(2, input.getPassword());
            cs.registerOutParameter(3, Types.INTEGER);
            cs.registerOutParameter(4, Types.INTEGER);

            cs.execute();

            role = cs.getInt(3);
            int tempId = cs.getInt(4);
            idInsegnante = (cs.wasNull()) ? -1 : tempId;
        } catch(SQLException e) {
            throw new DaoException("Errore durante il login: " + e.getMessage());
        }

        return new Credentials(input.getUsername(), input.getPassword(), Role.fromInt(role), idInsegnante);
    }
}
