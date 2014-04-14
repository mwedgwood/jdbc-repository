package com.github.mwedgwood.repository;

import com.github.mwedgwood.model.Model;
import com.google.common.base.Joiner;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import java.util.Collection;

public class CachingRepository<T extends Model> implements Repository<T> {

    private final Repository<T> delegateRep0sitory;
    private final Class<T> modelClass;
    private final Cache cache;

    public CachingRepository(Repository<T> delegateRep0sitory, Class<T> modelClass) {
        this.delegateRep0sitory = delegateRep0sitory;
        this.modelClass = modelClass;
        this.cache = CacheManager.getCacheManager("manager").getCache("distributed");
    }

    @Override
    public T findById(Integer id) {
        T entitiyFromCache = getFromCache(id);
        if (entitiyFromCache != null) {
            return entitiyFromCache;
        }
        T entity = delegateRep0sitory.findById(id);
        cache.put(new Element(cacheKey(id), entity));
        return entity;
    }

    @Override
    public Collection<T> findAll() {
        return null;
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

    protected final String cacheKey(Integer entityId) {
        return Joiner.on("|").join(modelClass.getName(), entityId);
    }

    protected final T getFromCache(Integer id) {
        Element element = cache.get(cacheKey(id));
        if (element != null) {
            return (T) element.getObjectValue();
        }
        return null;
    }
}
