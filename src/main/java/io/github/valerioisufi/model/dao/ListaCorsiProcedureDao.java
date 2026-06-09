package io.github.valerioisufi.model.dao;

import io.github.valerioisufi.exception.DaoException;
import io.github.valerioisufi.model.domain.Corso;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ListaCorsiProcedureDao {

    public List<Corso> listaCorsi() throws DaoException {
        List<Corso> corsi = new ArrayList<>();

        try {
            Connection conn = DbConnection.getInstance().getConnection();
            CallableStatement cs = conn.prepareCall("{call lista_corsi()}");

            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                Corso corso = new Corso(
                        rs.getString("NomeLivello"),
                        rs.getInt("Codice"),
                        rs.getDate("DataAttivazione").toLocalDate(),
                        rs.getInt("NumAllievi"),
                        rs.getString("Libro"),
                        rs.getBoolean("EsameObbligatorio")
                );
                corsi.add(corso);
            }

        } catch (SQLException e) {
            throw new DaoException("Errore durante il recupero della lista dei corsi: " + e.getMessage());
        }

        return corsi;
    }
}
