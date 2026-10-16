package com.example.qiyegongdan.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.qiyegongdan.common.Result;
import com.example.qiyegongdan.entity.Ticket;
import com.example.qiyegongdan.entity.TicketType;
import com.example.qiyegongdan.mapper.TicketMapper;
import com.example.qiyegongdan.mapper.TicketTypeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 统计报表服务
 */
@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final TicketMapper ticketMapper;
    private final TicketTypeMapper ticketTypeMapper;

    public Map<String, Object> overview() {
        long total = ticketMapper.selectCount(null);
        long newCount = ticketMapper.selectCount(new LambdaQueryWrapper<Ticket>().eq(Ticket::getStatus, "NEW"));
        long processing = ticketMapper.selectCount(new LambdaQueryWrapper<Ticket>()
                .in(Ticket::getStatus, "ASSIGNED", "PROCESSING"));
        long closed = ticketMapper.selectCount(new LambdaQueryWrapper<Ticket>().eq(Ticket::getStatus, "CLOSED"));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total", total);
        result.put("new", newCount);
        result.put("processing", processing);
        result.put("closed", closed);
        return result;
    }

    /** 按状态统计 */
    public List<Map<String, Object>> byStatus() {
        return countGroup(Ticket::getStatus);
    }

    /** 按优先级统计 */
    public List<Map<String, Object>> byPriority() {
        return countGroup(Ticket::getPriority);
    }

    /** 按类型统计 */
    public List<Map<String, Object>> byType() {
        List<Ticket> all = ticketMapper.selectList(null);
        Map<Long, Long> grouped = all.stream()
                .collect(Collectors.groupingBy(Ticket::getTypeId, Collectors.counting()));
        Map<Long, String> typeNames = ticketTypeMapper.selectList(null).stream()
                .collect(Collectors.toMap(TicketType::getId, TicketType::getName));
        List<Map<String, Object>> list = new ArrayList<>();
        grouped.forEach((typeId, count) -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("name", typeNames.getOrDefault(typeId, "未知"));
            m.put("value", count);
            list.add(m);
        });
        return list;
    }

    /** 近7天工单创建趋势 */
    public List<Map<String, Object>> trend() {
        LocalDateTime today = LocalDateTime.now().toLocalDate().atStartOfDay();
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDateTime start = today.minusDays(i);
            LocalDateTime end = start.plusDays(1);
            long count = ticketMapper.selectCount(new LambdaQueryWrapper<Ticket>()
                    .ge(Ticket::getCreatedAt, start).lt(Ticket::getCreatedAt, end));
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("date", start.toLocalDate().toString());
            m.put("count", count);
            list.add(m);
        }
        return list;
    }

    private List<Map<String, Object>> countGroup(java.util.function.Function<Ticket, String> extractor) {
        List<Ticket> all = ticketMapper.selectList(null);
        Map<String, Long> grouped = all.stream()
                .collect(Collectors.groupingBy(extractor, Collectors.counting()));
        List<Map<String, Object>> list = new ArrayList<>();
        grouped.forEach((key, count) -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("name", key);
            m.put("value", count);
            list.add(m);
        });
        return list;
    }
}