package com.example.dbq;

import java.util.UUID;

public class MessageDto {

    private String message;
    private UUID sender;
    private UUID receiver;

    public MessageDto(String message) {
        this.message = message;
        this.sender = UUID.randomUUID();
        this.receiver = UUID.randomUUID();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UUID getSender() {
        return sender;
    }

    public void setSender(UUID sender) {
        this.sender = sender;
    }

    public UUID getReceiver() {
        return receiver;
    }

    public void setReceiver(UUID receiver) {
        this.receiver = receiver;
    }

    @Override
    public String toString() {
        return "MessageDto{" +
                "message='" + message + '\'' +
                ", sender=" + sender +
                ", receiver=" + receiver +
                '}';
    }
}
