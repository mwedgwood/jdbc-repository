package com.github.mwedgwood.repository;

import com.github.mwedgwood.db.DBClient;
import com.github.mwedgwood.db.MetaDataCache;
import com.github.mwedgwood.db.StatementBuilder;
import com.github.mwedgwood.db.sql.Constraints;
import com.github.mwedgwood.db.sql.Sql;
import com.github.mwedgwood.model.Model;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

public abstract class AbstractRepository<T extends Model> implements Repository<T> {

    protected final Class<T> modelClass;
    protected final DBClient<T> dbClient;
    protected final MetaDataCache.MetaData metaData;

    protected AbstractRepository() {
        this.modelClass = inferModelClass();
        this.metaData = MetaDataCache.getInstance().getMetaDataForClass(modelClass);
        this.dbClient = new DBClient<>(modelClass);
    }

    @Override
    public T findById(final Integer id) {
        return dbClient.execute(new StatementBuilder() {
            @Override
            public PreparedStatement sql(Connection con) throws SQLException {
                PreparedStatement preparedStatement = con.prepareStatement(Sql.select(metaData.getColumns()).from(metaData.getTableName()).where(Constraints.eq("id")).toSql());
                preparedStatement.setInt(1, id);
                return preparedStatement;
            }
        });
    }

    @Override
    public Collection<T> findAll() {
        return dbClient.executeList(new StatementBuilder() {
            @Override
            public PreparedStatement sql(Connection con) throws SQLException {
                return con.prepareStatement(Sql.select(metaData.getColumns()).from(metaData.getTableName()).toSql());
            }
        });
    }

    @Override
    public Collection<T> findByExample(T example) {
        return null;
    }

    @Override
    public void save(T entity) {
    }

    @Override
    public void delete(T entity) {
    }

    @Override
    public void update(T entity) {
    }

    @SuppressWarnings("unchecked")
    private Class<T> inferModelClass() {
        Type type = null;
        Class aClass = this.getClass();
        while (aClass.getSuperclass() != null && aClass != AbstractRepository.class) {
            type = aClass.getGenericSuperclass();
            aClass = aClass.getSuperclass();
        }
        assert type != null;
        return (Class<T>) ((ParameterizedType) type).getActualTypeArguments()[0];
    }

}