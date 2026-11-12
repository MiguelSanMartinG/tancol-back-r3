package com.main.tancol.dashboard.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.util.LinkedHashMap;
import java.util.Map;

public class NativeQueryBuilder {

    private final StringBuilder sql = new StringBuilder();
    private final Map<String, Object> params = new LinkedHashMap<>();

    public NativeQueryBuilder append(String fragment) {
        sql.append(fragment);
        return this;
    }

    public NativeQueryBuilder param(String name, Object value) {
        params.put(name, value);
        return this;
    }

    public Query create(EntityManager entityManager) {
        Query query = entityManager.createNativeQuery(sql.toString());
        params.forEach(query::setParameter);
        return query;
    }

    @Override
    public String toString() {
        return sql.toString();
    }
}
