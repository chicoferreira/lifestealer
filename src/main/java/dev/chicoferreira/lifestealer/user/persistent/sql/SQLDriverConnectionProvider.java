package dev.chicoferreira.lifestealer.user.persistent.sql;

public abstract class SQLDriverConnectionProvider implements SQLConnectionProvider {

    public abstract String getDriverClassName();

    public SQLDriverConnectionProvider() {
        try {
            Class.forName(getDriverClassName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load driver class", e);
        }
    }

}
