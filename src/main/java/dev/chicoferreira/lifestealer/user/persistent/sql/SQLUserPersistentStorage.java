package dev.chicoferreira.lifestealer.user.persistent.sql;

import dev.chicoferreira.lifestealer.user.LifestealerUser;
import dev.chicoferreira.lifestealer.user.persistent.UserPersistentStorage;
import dev.chicoferreira.lifestealer.user.rules.LifestealerUserRules;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.UUID;

public class SQLUserPersistentStorage implements UserPersistentStorage {

    private final SQLConnectionProvider connectionProvider;

    private static final String CREATE_TABLE_STATEMENT = """
            CREATE TABLE lifestealer_users (uuid  VARCHAR(36) PRIMARY KEY,
                hearts                    INT,
                ban_instant               TIMESTAMP,
                ban_duration_seconds      BIGINT,
                max_hearts_modifier       INT,
                min_hearts_modifier       INT,
                ban_time_modifier_seconds BIGINT,
                return_hearts_modifier    INT
            )""";
    private static final String SELECT_USER_STATEMENT = "SELECT * FROM lifestealer_users WHERE uuid = ?";
    private static final String SELECT_USER_UUID_STATEMENT = "SELECT uuid FROM lifestealer_users";
    private static final String INSERT_USER_STATEMENT = "INSERT INTO lifestealer_users (uuid, hearts, ban_instant, ban_duration_seconds, max_hearts_modifier, min_hearts_modifier, ban_time_modifier_seconds, return_hearts_modifier) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_USER_STATEMENT = "UPDATE lifestealer_users SET hearts = ?, ban_instant = ?, ban_duration_seconds = ?, max_hearts_modifier = ?, min_hearts_modifier = ?, ban_time_modifier_seconds = ?, return_hearts_modifier = ? WHERE uuid = ?";

    public SQLUserPersistentStorage(SQLConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public String getDatabaseName() {
        return "SQL " + connectionProvider.getClass().getSimpleName();
    }

    @Override
    public void init() throws Exception {
        try (Connection connection = connectionProvider.getConnection()) {
            if (!tableExists(connection)) {
                createTable(connection);
            }
        }
    }

    @Override
    public void close() throws SQLException {
        this.connectionProvider.close();
    }

    private boolean tableExists(Connection connection) throws Exception {
        try (var resultSet = connection.getMetaData().getTables(null, null, "%", null)) {
            while (resultSet.next()) {
                if (resultSet.getString("TABLE_NAME").equalsIgnoreCase("lifestealer_users")) {
                    return true;
                }
            }
            return false;
        }
    }

    private void createTable(Connection connection) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(CREATE_TABLE_STATEMENT)) {
            statement.execute();
        }
    }

    private boolean userExists(UUID uuid, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_USER_UUID_STATEMENT)) {
            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    if (resultSet.getString("uuid").equals(uuid.toString())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public LifestealerUser loadUser(UUID uuid) throws Exception {
        try (Connection connection = connectionProvider.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SELECT_USER_STATEMENT)) {
                statement.setString(1, uuid.toString());
                try (var resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        @Nullable LifestealerUser.Ban ban = null;
                        Timestamp banInstant = resultSet.getTimestamp("ban_instant");
                        if (banInstant != null) {
                            ban = new LifestealerUser.Ban(banInstant.toInstant(), Duration.ofSeconds(resultSet.getLong("ban_duration_seconds")));
                        }

                        LifestealerUserRules modifierRules = new LifestealerUserRules.Builder().maxHearts(resultSet.getInt("max_hearts_modifier")).minHearts(resultSet.getInt("min_hearts_modifier")).banTime(Duration.ofSeconds(resultSet.getLong("ban_time_modifier_seconds"))).returnHearts(resultSet.getInt("return_hearts_modifier")).build();

                        return new LifestealerUser(UUID.fromString(resultSet.getString("uuid")), resultSet.getInt("hearts"), ban, modifierRules);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void saveUser(LifestealerUser user) throws Exception {
        try (Connection connection = connectionProvider.getConnection()) {
            if (this.userExists(user.getUuid(), connection)) {
                try (PreparedStatement statement = connection.prepareStatement(UPDATE_USER_STATEMENT)) {
                    statement.setInt(1, user.getHearts());
                    if (user.getInternalBan() != null) {
                        statement.setTimestamp(2, Timestamp.from(user.getInternalBan().start()));
                        statement.setLong(3, user.getInternalBan().duration().getSeconds());
                    } else {
                        statement.setTimestamp(2, null);
                        statement.setInt(3, 0);
                    }
                    statement.setInt(4, user.getRulesModifier().maxHearts());
                    statement.setInt(5, user.getRulesModifier().minHearts());
                    statement.setLong(6, user.getRulesModifier().banTime().getSeconds());
                    statement.setInt(7, user.getRulesModifier().returnHearts());
                    statement.setString(8, user.getUuid().toString());
                    statement.execute();
                }
            } else {
                try (PreparedStatement statement = connection.prepareStatement(INSERT_USER_STATEMENT)) {
                    statement.setString(1, user.getUuid().toString());
                    statement.setInt(2, user.getHearts());
                    if (user.getInternalBan() != null) {
                        statement.setTimestamp(3, Timestamp.from(user.getInternalBan().start()));
                        statement.setLong(4, user.getInternalBan().duration().getSeconds());
                    } else {
                        statement.setTimestamp(3, null);
                        statement.setInt(4, 0);
                    }
                    statement.setInt(5, user.getRulesModifier().maxHearts());
                    statement.setInt(6, user.getRulesModifier().minHearts());
                    statement.setLong(7, user.getRulesModifier().banTime().getSeconds());
                    statement.setInt(8, user.getRulesModifier().returnHearts());
                    statement.execute();
                }
            }
        }
    }
}
