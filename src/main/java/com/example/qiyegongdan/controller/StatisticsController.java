package com.example.qiyegongdan.controller;

import com.example.qiyegongdan.common.Result;
import com.example.qiyegongdan.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 统计报表控制器
 */
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/overview")
    public Result<Map<String, Object>> overview() {
        return Result.success(statisticsService.overview());
    }

    @GetMapping("/by-status")
    public Result<List<Map<String, Object>>> byStatus() {
        return Result.success(statisticsService.byStatus());
    }

    @GetMapping("/by-priority")
    public Result<List<Map<String, Object>>> byPriority() {
        return Result.success(statisticsService.byPriority());
    }

    @GetMapping("/by-type")
    public Result<List<Map<String, Object>>> byType() {
        return Result.success(statisticsService.byType());
    }

    @GetMapping("/trend")
    public Result<List<Map<String, Object>>> trend() {
        return Result.success(statisticsService.trend());
    }
}