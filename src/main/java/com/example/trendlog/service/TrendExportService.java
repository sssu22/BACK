package com.example.trendlog.service;

import com.example.trendlog.dto.response.trend.TrendCsvDto;
import com.example.trendlog.repository.trend.TrendRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrendExportService {
    private final TrendRepository trendRepository;

    public void exportAllTrendsToCsv(){
        List<TrendCsvDto> trends = trendRepository.findAllTrendsForCsv();
        File file = new File("all_trends.csv"); // 파일 객체 생성
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("trend_id,title,category\n");
            for (TrendCsvDto trend : trends) {
                writer.write(String.format("%d,%s,%s\n",
                        trend.getTrendId(),
                        trend.getTitle().replace(",", " "), // 콤마 방지
                        trend.getCategory()));
            }
            log.info("CSV 파일 저장 완료: {}", file.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("CSV 파일 저장 실패", e);
        }
    }
}
