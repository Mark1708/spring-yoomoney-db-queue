package com.example.starter.dao;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import com.example.dbqueue.api.TaskRecord;
import com.example.dbqueue.config.QueueTableSchema;
import com.example.dbqueue.dao.QueuePickTaskDao;
import com.example.dbqueue.settings.FailRetryType;
import com.example.dbqueue.settings.FailureSettings;
import com.example.dbqueue.settings.PollSettings;
import com.example.dbqueue.settings.QueueLocation;

import javax.annotation.Nonnull;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Database access object to pick tasks in the queue for PostgreSQL database type.
 */
public class PostgresQueuePickTaskDao implements QueuePickTaskDao {

    private String pickTasksSql;
    private final MapSqlParameterSource pickTaskSqlPlaceholders;
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final QueueTableSchema queueTableSchema;

    /**
     * Constructor
     *
     * @param jdbcTemplate     spring jdbc template
     * @param queueTableSchema table schema
     * @param queueLocation    queue location
     * @param failureSettings  failure settings
     */
    public PostgresQueuePickTaskDao(
            @Nonnull JdbcOperations jdbcTemplate,
            @Nonnull QueueTableSchema queueTableSchema,
            @Nonnull QueueLocation queueLocation,
            @Nonnull FailureSettings failureSettings,
            @Nonnull PollSettings pollSettings
    ) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(requireNonNull(jdbcTemplate));
        this.queueTableSchema = requireNonNull(queueTableSchema);
        pickTasksSql = createPickTasksSql(queueLocation, failureSettings);
        pickTaskSqlPlaceholders = new MapSqlParameterSource()
                .addValue("queueName", queueLocation.getQueueId().asString())
                .addValue("retryInterval", failureSettings.getRetryInterval().toMillis())
                .addValue("batchSize", pollSettings.getBatchSize());
        failureSettings.registerObserver((oldValue, newValue) -> {
            pickTasksSql = createPickTasksSql(queueLocation, newValue);
            pickTaskSqlPlaceholders.addValue("retryInterval", newValue.getRetryInterval().toMillis());
        });
        pollSettings.registerObserver((oldValue, newValue) -> {
            pickTasksSql = createPickTasksSql(queueLocation, failureSettings);
            pickTaskSqlPlaceholders.addValue("batchSize", newValue.getBatchSize());
        });
    }

    @Nonnull
    @Override
    public List<TaskRecord> pickTasks() {
        return requireNonNull(jdbcTemplate.execute(
                pickTasksSql,
                pickTaskSqlPlaceholders,
                (PreparedStatement ps) -> {
                    try (ResultSet rs = ps.executeQuery()) {
                        List<TaskRecord> taskRecords = new LinkedList<>();
                        while (rs.next()) {
                            taskRecords.add(createTaskRecord(rs));
                        }
                        return taskRecords;
                    }
                }
        ));
    }

    private TaskRecord createTaskRecord(ResultSet rs) throws SQLException {
        Map<String, String> additionalData = new LinkedHashMap<>();
        queueTableSchema.getExtFields().forEach(key -> {
            try {
                additionalData.put(key, rs.getString(key));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        return TaskRecord.builder()
                .withId(rs.getLong(queueTableSchema.getIdField()))
                .withCreatedAt(getZonedDateTime(rs, queueTableSchema.getCreatedAtField()))
                .withNextProcessAt(getZonedDateTime(rs, queueTableSchema.getNextProcessAtField()))
                .withPayload(rs.getString(queueTableSchema.getPayloadField()))
                .withAttemptsCount(rs.getLong(queueTableSchema.getAttemptField()))
                .withReenqueueAttemptsCount(rs.getLong(queueTableSchema.getReenqueueAttemptField()))
                .withTotalAttemptsCount(rs.getLong(queueTableSchema.getTotalAttemptField()))
                .withExtData(additionalData).build();
    }

    private String createPickTasksSql(@Nonnull QueueLocation location, @Nonnull FailureSettings failureSettings) {
        return "WITH cte AS (" +
                "SELECT " + queueTableSchema.getIdField() + " " +
                "FROM " + location.getTableName() + " " +
                "WHERE " + queueTableSchema.getQueueNameField() + " = :queueName " +
                "  AND " + queueTableSchema.getNextProcessAtField() + " <= now() " +
                " ORDER BY " + queueTableSchema.getNextProcessAtField() + " ASC " +
                "LIMIT :batchSize " +
                "FOR UPDATE SKIP LOCKED) " +
                "UPDATE " + location.getTableName() + " q " +
                "SET " +
                "  " + queueTableSchema.getNextProcessAtField() + " = " +
                getNextProcessTimeSql(failureSettings.getRetryType(), queueTableSchema) + ", " +
                "  " + queueTableSchema.getAttemptField() + " = " + queueTableSchema.getAttemptField() + " + 1, " +
                "  " + queueTableSchema.getTotalAttemptField() + " = " + queueTableSchema.getTotalAttemptField() + " + 1 " +
                "FROM cte " +
                "WHERE q." + queueTableSchema.getIdField() + " = cte." + queueTableSchema.getIdField() + " " +
                "RETURNING q." + queueTableSchema.getIdField() + ", " +
                "q." + queueTableSchema.getPayloadField() + ", " +
                "q." + queueTableSchema.getAttemptField() + ", " +
                "q." + queueTableSchema.getReenqueueAttemptField() + ", " +
                "q." + queueTableSchema.getTotalAttemptField() + ", " +
                "q." + queueTableSchema.getCreatedAtField() + ", " +
                "q." + queueTableSchema.getNextProcessAtField() +
                (queueTableSchema.getExtFields().isEmpty() ? "" : queueTableSchema.getExtFields().stream()
                        .map(field -> "q." + field).collect(Collectors.joining(", ", ", ", "")));
    }

    private ZonedDateTime getZonedDateTime(ResultSet rs, String time) throws SQLException {
        return ZonedDateTime.ofInstant(rs.getTimestamp(time).toInstant(), ZoneId.systemDefault());
    }


    @Nonnull
    private String getNextProcessTimeSql(@Nonnull FailRetryType failRetryType, QueueTableSchema queueTableSchema) {
        requireNonNull(failRetryType);
        return switch (failRetryType) {
            case FailRetryType.GEOMETRIC_BACKOFF ->
                    "now() + power(2, " + queueTableSchema.getAttemptField() + ") * :retryInterval * INTERVAL '1 MILLISECOND'";
            case FailRetryType.ARITHMETIC_BACKOFF ->
                    "now() + (1 + (" + queueTableSchema.getAttemptField() + " * 2)) * :retryInterval * INTERVAL '1 MILLISECOND'";
            case FailRetryType.LINEAR_BACKOFF -> "now() + :retryInterval * INTERVAL '1 MILLISECOND'";
        };
    }
}
