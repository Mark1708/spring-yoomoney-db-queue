package com.example.common;

import java.time.LocalDateTime;
import java.util.Objects;

public class Stat {

    private Long id;
    private LocalDateTime sentAt;
    private LocalDateTime receivedAt;

    public Stat(Long id, LocalDateTime sentAt, LocalDateTime receivedAt) {
        this.id = id;
        this.sentAt = sentAt;
        this.receivedAt = receivedAt;
    }

    public Stat() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stat stat = (Stat) o;
        return Objects.equals(id, stat.id) && Objects.equals(sentAt, stat.sentAt) && Objects.equals(receivedAt, stat.receivedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sentAt, receivedAt);
    }
}
