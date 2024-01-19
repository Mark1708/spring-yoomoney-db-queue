package com.example.common;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class MessageDto {

    private String message;
    private LocalDateTime createdAt;

    public MessageDto(String message) {
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }
}
