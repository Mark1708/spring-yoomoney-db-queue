package com.example.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Stat {

    private Long id;
    private LocalDateTime sentAt;
    private LocalDateTime receivedAt;
}
