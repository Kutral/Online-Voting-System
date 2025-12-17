package com.voting.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Election {
    private int id;
    private String name;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
