package com.github.mwedgwood.db;

import com.google.common.base.CaseFormat;

import javax.persistence.Table;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.reflections.ReflectionUtils.*;

public final class MetaDataCache {

    Map<Class<?>, MetaData> cache;

    private static class SingletonHolder {
        private static final MetaDataCache INSTANCE = new MetaDataCache();
    }

    private MetaDataCache() {
        cache = new ConcurrentHashMap<>();
    }

    public static MetaDataCache getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public MetaData getMetaDataForClass(Class<?> klass) {
        if (cache.get(klass) == null) {
            cache.put(klass, new MetaData(klass));
        }
        return cache.get(klass);
    }


    public static class MetaData {
        private Class<?> klass;
        private String tableName;
        private Map<String, Method> columnToSetter;

        MetaData(Class<?> klass) {
            this.klass = klass;
            this.tableName = initTableName();
            this.columnToSetter = initColumnToSetterMap();
        }

        public String getTableName() {
            return tableName;
        }

        public Set<String> getColumns() {
            return columnToSetter.keySet();
        }

        public Method setterForColumn(String columnName) {
            return columnToSetter.get(columnName);
        }

        String initTableName() {
            Table tableAnnotation = klass.getAnnotation(Table.class);
            if (tableAnnotation != null) {
                return tableAnnotation.name();
            }
            return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, klass.getSimpleName());
        }

        Map<String, Method> initColumnToSetterMap() {
            HashMap<String, Method> result = new HashMap<>();
            for (Method setter : setters()) {
                result.put(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, setter.getName().replaceFirst("set", "")), setter);
            }
            return result;
        }

        Set<Method> setters() {
            return getAllMethods(klass, withModifier(Modifier.PUBLIC), withPrefix("set"), withParametersCount(1));
        }
    }

}
