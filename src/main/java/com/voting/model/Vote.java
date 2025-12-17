package com.voting.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vote {
    private int id;
    private int userId;
    private int electionId;
    private int candidateId;
    private LocalDateTime timestamp;
}
