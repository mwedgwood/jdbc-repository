package com.github.mwedgwood.db.sql;

import com.google.common.base.Joiner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Sql {
    public static SqlBuilder select(Set<String> columns) {
        return new SqlBuilder().select(columns);
    }

    public static class SqlBuilder {
        private String tableName;
        private Set<String> columns = new HashSet<>();
        private Set<Constraints.Constraint> constraints = new HashSet<>();

        private SqlBuilder() {
        }

        public SqlBuilder select(String... columns) {
            this.columns.addAll(Arrays.asList(columns));
            return this;
        }

        public SqlBuilder select(Set<String> columns) {
            this.columns.addAll(columns);
            return this;
        }

        public SqlBuilder from(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public SqlBuilder where(Constraints.Constraint constraint) {
            this.constraints.add(constraint);
            return this;
        }

        public String toSql() {
            return "SELECT " + Joiner.on(",").skipNulls().join(columns) + " FROM " + tableName + " WHERE " + Joiner.on(",").skipNulls().join(constraints);
        }

    }
}
