package com.github.mwedgwood.db;

import java.sql.ResultSet;

public interface ResultSetMapper<T> {

    T map(ResultSet resultSet);

}
