package com.github.mwedgwood.db.sql;

public class Constraints {
    public static Constraint eq(String column) {
        return new Constraint(column);
    }

    public static class Constraint {
        private final String column;

        public Constraint(String column) {
            this.column = column;
        }

        @Override
        public String toString() {
            return column + " = ?";
        }
    }
}
