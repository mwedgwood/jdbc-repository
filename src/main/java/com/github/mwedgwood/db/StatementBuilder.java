package com.github.mwedgwood.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface StatementBuilder {

    PreparedStatement sql(Connection con) throws SQLException;

}
