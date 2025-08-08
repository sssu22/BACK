package com.example.trendlog.dto.response.trend;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TrendTagResponse {
    private List<String> tags;
}
