package com.example.trendlog.service.trend;

import com.example.trendlog.global.exception.AppException;
import com.example.trendlog.global.exception.code.CsvErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TrendRecommendCsvImportService {
    private final TrendRecommendationService trendRecommendationService;

    public void importFromCsv(String filePath) {
        Map<UUID, List<Long>> userRecommendations = new LinkedHashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine(); // 헤더 skip
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                UUID userId = UUID.fromString(parts[0]);
                Long trendId = Long.parseLong(parts[1]);

                userRecommendations.computeIfAbsent(userId, k -> new ArrayList<>()).add(trendId);
            }

            for (Map.Entry<UUID, List<Long>> entry : userRecommendations.entrySet()) {
                trendRecommendationService.saveRecommendations(entry.getKey(), entry.getValue());
            }

        } catch (IOException e) {
            throw new AppException(CsvErrorCode.RECOMMEND_IMPORT_FAIL);
        }
    }
}
