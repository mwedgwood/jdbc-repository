package com.github.mwedgwood.db;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBClient<T> {

    private final ResultSetMapper<T> resultSetMapper;
    private final DataSource dataSource;

    public DBClient(ResultSetMapper<T> resultSetMapper) {
        this.resultSetMapper = resultSetMapper;
        this.dataSource = DataSourceFactory.getInstance().getDataSource();
    }

    public final T query(StatementBuilder statementBuilder) {
        List<T> results = queryList(statementBuilder);
        return results.isEmpty() ? null : results.get(0);
    }

    public final List<T> queryList(StatementBuilder statementBuilder) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = dataSource.getConnection();
            preparedStatement = statementBuilder.sql(connection);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<T> results = new ArrayList<>();
            while (resultSet.next()) {
                results.add(resultSetMapper.map(resultSet));
            }
            return results;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(preparedStatement, connection);
        }
    }

    void close(PreparedStatement preparedStatement, Connection connection) {
        try {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
