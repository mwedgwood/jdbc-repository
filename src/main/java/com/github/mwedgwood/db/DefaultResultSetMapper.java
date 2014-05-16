package com.github.mwedgwood.db;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class DefaultResultSetMapper<T> implements ResultSetMapper<T> {

    private final Class<T> modelClass;
    private final MetaDataCache.MetaData metaData;

    public DefaultResultSetMapper(Class<T> modelClass, MetaDataCache.MetaData metaData) {
        this.modelClass = modelClass;
        this.metaData = metaData;
    }

    @Override
    public T map(ResultSet resultSet) {
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
            throw new RuntimeException(e);
        }
    }

}
