package com.example.trendlog.domain.trend;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "trend_prediction")
public class TrendPrediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long trendId;

    private Double increaseRate;

    private Double confidence;

    private LocalDate predictionDate;
}
