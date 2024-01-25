package com.example.starter.config;

import com.example.dbqueue.config.DatabaseAccessLayer;
import com.example.dbqueue.config.QueueShard;
import com.example.dbqueue.config.QueueShardId;
import com.example.dbqueue.config.QueueTableSchema;
import com.example.dbqueue.dao.QueueDao;
import com.example.dbqueue.dao.QueuePickTaskDao;
import com.example.dbqueue.settings.ExtSettings;
import com.example.dbqueue.settings.FailureSettings;
import com.example.dbqueue.settings.PollSettings;
import com.example.dbqueue.settings.ProcessingSettings;
import com.example.dbqueue.settings.QueueConfig;
import com.example.dbqueue.settings.QueueId;
import com.example.dbqueue.settings.QueueLocation;
import com.example.dbqueue.settings.QueueSettings;
import com.example.dbqueue.settings.ReenqueueSettings;
import com.example.starter.SpringDatabaseAccessLayer;
import com.example.starter.dao.PostgresQueueDao;
import com.example.starter.dao.PostgresQueuePickTaskDao;
import com.example.starter.properties.DbQueueProperties;
import com.example.starter.properties.ExtSettingsProperties;
import com.example.starter.properties.FailureSettingsProperties;
import com.example.starter.properties.PollSettingsProperties;
import com.example.starter.properties.ProcessingSettingsProperties;
import com.example.starter.properties.QueueConfigurationProperties;
import com.example.starter.properties.ReenqueueSettingsProperties;
import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Nonnull;
import java.util.List;

import static java.util.Collections.singletonList;

@Configuration
@EnableConfigurationProperties(
        value = {
                DbQueueProperties.class,
                QueueConfigurationProperties.class
        }
)
@ConditionalOnProperty(
        name = {"spring.db-queue.enabled"},
        havingValue = "true",
        matchIfMissing = false
)
public class DbQueueAutoConfiguration {

        private static final Logger  log = org.slf4j.LoggerFactory.getLogger(DbQueueAutoConfiguration.class);

        private final DbQueueProperties dbQueueProperties;

        public DbQueueAutoConfiguration(DbQueueProperties dbQueueProperties) {
                this.dbQueueProperties = dbQueueProperties;
        }

        @Bean
        public String queueName() {
                if (dbQueueProperties.getConfig().size() > 1) {
                        log.warn("Only one queue is supported, please create own configuration!");
                }
                return dbQueueProperties.getConfig()
                        .keySet()
                        .stream()
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Queue configuration is required!"));
        }

        @Bean
        public QueueConfigurationProperties configurationProperties(String queueName) {
                return dbQueueProperties.getConfig().get(queueName);
        }

        @Bean
        public SpringDatabaseAccessLayer databaseAccessLayer(
                JdbcOperations jdbcOperations,
                TransactionOperations transactionOperations
        ) {
                return new SpringDatabaseAccessLayer(
                        QueueTableSchema.builder().build(),
                        jdbcOperations,
                        transactionOperations
                );
        }

        @Bean
        public List<QueueShard<DatabaseAccessLayer>> queueShards(
                DatabaseAccessLayer databaseAccessLayer,
                QueueConfigurationProperties configurationProperties
        ) {
                return configurationProperties.getShards()
                        .stream()
                        .map(shard -> new QueueShard<>(
                                new QueueShardId(shard), databaseAccessLayer)
                        )
                        .toList();
        }

        @Bean
        @ConditionalOnMissingBean
        public QueueId queueId(String queueName) {
                return new QueueId(queueName);
        }

        @Bean
        @ConditionalOnMissingBean
        public QueueSettings queueSettings(
                QueueConfigurationProperties configurationProperties
        ) {
                ProcessingSettingsProperties processingSettings = configurationProperties.getProcessingSettings();
                PollSettingsProperties pollSettings = configurationProperties.getPollSettings();
                FailureSettingsProperties failureSettings = configurationProperties.getFailureSettings();
                ReenqueueSettingsProperties reenqueueSettings = configurationProperties.getReenqueueSettings();
                ExtSettingsProperties extSettings = configurationProperties.getExtSettings();

                return QueueSettings.builder()
                        .withProcessingSettings(
                                ProcessingSettings.builder()
                                        .withProcessingMode(processingSettings.getProcessingMode())
                                        .withThreadCount(processingSettings.getThreadCount())
                                        .build()
                        )
                        .withPollSettings(
                                PollSettings.builder()
                                        .withBetweenTaskTimeout(pollSettings.getBetweenTaskTimeout())
                                        .withNoTaskTimeout(pollSettings.getNoTaskTimeout())
                                        .withFatalCrashTimeout(pollSettings.getFatalCrashTimeout())
                                        .withBatchSize(pollSettings.getBatchSize())
                                        .build()
                        )
                        .withFailureSettings(
                                FailureSettings.builder()
                                        .withRetryType(failureSettings.getRetryType())
                                        .withRetryInterval(failureSettings.getRetryInterval())
                                        .build()
                        )
                        .withReenqueueSettings(
                                ReenqueueSettings.builder()
                                        .withRetryType(reenqueueSettings.getRetryType())
                                        .build()
                        )
                        .withExtSettings(
                                ExtSettings.builder()
                                        .withSettings(extSettings.getSettings())
                                        .build()
                        )
                        .build();
        }

        @Bean
        @ConditionalOnMissingBean
        public QueueLocation queueLocation(
                QueueId queueId,
                QueueConfigurationProperties configurationProperties
        ) {
                return QueueLocation.builder()
                        .withTableName(configurationProperties.getLocation())
                        .withQueueId(queueId)
                        .build();
        }

        @Bean
        @ConditionalOnMissingBean
        public QueueConfig queueConfig(
                QueueLocation queueLocation,
                QueueSettings queueSettings
        ) {
                return new QueueConfig(queueLocation, queueSettings);
        }

//        @Bean
//        @ConditionalOnMissingBean(QueueDao.class)
//        @ConditionalOnBean(JdbcTemplate.class)
//        public QueueDao postgresQueueDao(JdbcTemplate jdbcTemplate) {
//                return new PostgresQueueDao(jdbcTemplate, QueueTableSchema.builder().build());
//        }
//
//        @Bean
//        @ConditionalOnMissingBean(QueuePickTaskDao.class)
//        @ConditionalOnBean(JdbcTemplate.class)
//        public QueuePickTaskDao postgresQueuePickTaskDao(
//                JdbcTemplate jdbcTemplate,
//                QueueTableSchema queueTableSchema,
//                QueueLocation queueLocation,
//                FailureSettings failureSettings,
//                PollSettings pollSettings
//        ) {
//                return new PostgresQueuePickTaskDao(
//                        jdbcTemplate,
//                        queueTableSchema,
//                        queueLocation,
//                        failureSettings,
//                        pollSettings
//                );
//        }
}
