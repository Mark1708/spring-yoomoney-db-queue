package org.example.test.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import org.example.test.domain.Test;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;

@Data
public class TestResult implements Serializable {

    private Long id;
    private Long messageCount;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime startAt;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime endAt;
    private String status;
    private Duration duration;
    private Double rps;

    public TestResult(Test test) {
        this.id = test.getId();
        this.messageCount = test.getMessageCount();
        this.startAt = test.getStartAt();
        this.endAt = test.getEndAt();
        this.status = test.getStatus() == 0 ? "В процессе" : "Тест закончен";
        this.duration = Duration.between(startAt, endAt);
        this.rps = messageCount / ((double) duration.toMillis() * 1000);
    }
}
