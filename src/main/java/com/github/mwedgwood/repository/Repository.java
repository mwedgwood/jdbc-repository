package com.github.mwedgwood.repository;

import com.github.mwedgwood.model.Model;

import java.util.Collection;

public interface Repository<T extends Model> {

    T findById(Integer id);

    Collection<T> findAll();

    Collection<T> findByExample(T example);

    void save(T entity);

    void delete(T entity);

    void update(T entity);

}
