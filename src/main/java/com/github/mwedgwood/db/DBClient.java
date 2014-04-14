package com.github.mwedgwood.db;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBClient<T> {

    private final Class<T> modelClass;
    private final MetaDataCache.MetaData metaData;
    private final DataSource dataSource;

    // for unit tests
    DBClient(Class<T> modelClass, MetaDataCache.MetaData metaData, DataSource dataSource) {
        this.modelClass = modelClass;
        this.metaData = metaData;
        this.dataSource = dataSource;
    }

    public DBClient(Class<T> modelClass) {
        this(modelClass, MetaDataCache.getInstance().getMetaDataForClass(modelClass), DataSourceFactory.getInstance().getDataSource());
    }

    public final T execute(StatementBuilder statementBuilder) {
        List<T> results = executeList(statementBuilder);
        return results.isEmpty() ? null : results.get(0);
    }

    public final List<T> executeList(StatementBuilder statementBuilder) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = dataSource.getConnection();
            preparedStatement = statementBuilder.sql(connection);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<T> results = new ArrayList<>();
            while (resultSet.next()) {
                results.add(buildModel(resultSet));
            }
            return results;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(preparedStatement, connection);
        }
    }

    protected T buildModel(ResultSet resultSet) throws SQLException {
        try {
            T model = modelClass.newInstance();

            ResultSetMetaData rsMetaData = resultSet.getMetaData();
            for (int i = 1; i <= rsMetaData.getColumnCount(); i++) {
                String columnName = rsMetaData.getColumnName(i);
                Method setter = metaData.setterForColumn(columnName);
                Class<?> aClass = setter.getParameterTypes()[0];
                setter.invoke(model, aClass.cast(resultSet.getObject(columnName)));
            }

            return model;
        } catch (Exception e) {
            throw new SQLException(e);
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
