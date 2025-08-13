package com.example.trendlog.service.trend;

import com.example.trendlog.dto.response.trend.TrendCsvDto;
import com.example.trendlog.global.exception.AppException;
import com.example.trendlog.global.exception.code.CsvErrorCode;
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
//        File file = new File("ai-recommendation/all_trends.csv"); // 파일 객체 생성
        File file = new File("/shared/all_trends.csv");

        try (FileWriter writer = new FileWriter(file)) {
            writer.write("trend_id,title,category,description\n");
            for (TrendCsvDto trend : trends) {
                writer.write(String.format("%d,%s,%s,%s\n",
                        trend.getTrendId(),
                        trend.getTitle().replace(",", " "), // 콤마 방지
                        trend.getCategory(),
                        trend.getDescription().replace(",", " ")));
            }
            log.info("CSV 파일 저장 완료: {}", file.getAbsolutePath());
        } catch (IOException e) {
            throw new AppException(CsvErrorCode.TREND_EXPORT_FAIL);
        }
    }
}
