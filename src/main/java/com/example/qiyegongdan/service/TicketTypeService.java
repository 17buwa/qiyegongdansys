package com.example.qiyegongdan.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.qiyegongdan.common.BizException;
import com.example.qiyegongdan.entity.CustomField;
import com.example.qiyegongdan.entity.TicketType;
import com.example.qiyegongdan.mapper.CustomFieldMapper;
import com.example.qiyegongdan.mapper.TicketTypeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 工单类型服务（含自定义字段）
 */
@Service
@RequiredArgsConstructor
public class TicketTypeService {

    private final TicketTypeMapper ticketTypeMapper;
    private final CustomFieldMapper customFieldMapper;

    public List<Map<String, Object>> listEnabledTypes() {
        List<TicketType> types = ticketTypeMapper.selectList(new LambdaQueryWrapper<TicketType>()
                .eq(TicketType::getStatus, 1)
                .orderByAsc(TicketType::getId));
        List<Map<String, Object>> result = new ArrayList<>();
        for (TicketType type : types) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", type.getId());
            item.put("name", type.getName());
            item.put("code", type.getCode());
            item.put("description", type.getDescription());
            item.put("groupId", type.getGroupId());
            item.put("fields", listFields(type.getId()));
            result.add(item);
        }
        return result;
    }

    public List<CustomField> listFields(Long typeId) {
        return customFieldMapper.selectList(new LambdaQueryWrapper<CustomField>()
                .eq(CustomField::getTicketTypeId, typeId)
                .orderByAsc(CustomField::getSort));
    }

    public TicketType createType(TicketType type) {
        ticketTypeMapper.insert(type);
        return type;
    }

    public void updateType(TicketType type) {
        ticketTypeMapper.updateById(type);
    }

    public void deleteType(Long id) {
        Long count = customFieldMapper.selectCount(new LambdaQueryWrapper<CustomField>()
                .eq(CustomField::getTicketTypeId, id));
        if (count > 0) {
            throw new BizException("该类型下存在自定义字段，请先删除字段");
        }
        ticketTypeMapper.deleteById(id);
    }

    public void addField(CustomField field) {
        customFieldMapper.insert(field);
    }

    public void updateField(CustomField field) {
        customFieldMapper.updateById(field);
    }

    public void deleteField(Long id) {
        customFieldMapper.deleteById(id);
    }
}