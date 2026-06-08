package io.github.valerioisufi.model.dao;

import io.github.valerioisufi.exception.DaoException;

public interface GenericProcedureDAO<I, O> {

    O execute(I input) throws DaoException;

}