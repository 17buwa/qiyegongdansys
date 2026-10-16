package com.example.qiyegongdan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 工单自定义字段实体
 */
@Data
@TableName("custom_field")
public class CustomField {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属工单类型id */
    private Long ticketTypeId;

    private String fieldName;

    private String fieldKey;

    /** 字段类型：text/number/select/date/textarea */
    private String fieldType;

    /** 选项，json数组文本（select类型用） */
    private String optionsJson;

    /** 是否必填：1是 0否 */
    private Integer required;

    /** 排序 */
    private Integer sort;
}