package com.example.common;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class MessageDto {

    private String message;
    private UUID sender;
    private UUID receiver;

    public MessageDto(String message) {
        this.message = message;
        this.sender = UUID.randomUUID();
        this.receiver = UUID.randomUUID();
    }
}
