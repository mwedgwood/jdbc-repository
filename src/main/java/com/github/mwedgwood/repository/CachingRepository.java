package com.github.mwedgwood.repository;

import com.github.mwedgwood.model.Model;
import com.google.common.base.Joiner;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import java.util.Collection;

public class CachingRepository<T extends Model> implements Repository<T> {

    private final Repository<T> delegateRepository;
    private final Class<T> modelClass;
    private final Cache cache;

    public CachingRepository(Repository<T> delegateRepository, Class<T> modelClass) {
        this.delegateRepository = delegateRepository;
        this.modelClass = modelClass;
        this.cache = CacheManager.getCacheManager("manager").getCache("distributed");
    }

    @Override
    public T findById(Integer id) {
        T entitiyFromCache = getFromCache(id);
        if (entitiyFromCache != null) {
            return entitiyFromCache;
        }
        T entity = delegateRepository.findById(id);
        cache.put(new Element(cacheKey(id), entity));
        return entity;
    }

    @Override
    public Collection<T> findAll() {
        // TODO implement
        return null;
    }

    @Override
    public Collection<T> findByExample(T example) {
        // TODO implement
        return null;
    }

    @Override
    public void save(T entity) {
        // TODO implement
    }

    @Override
    public void delete(T entity) {
        // TODO implement
    }

    @Override
    public void update(T entity) {
        // TODO implement
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
