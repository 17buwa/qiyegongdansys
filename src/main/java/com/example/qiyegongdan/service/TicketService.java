package com.example.qiyegongdan.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.qiyegongdan.common.BizException;
import com.example.qiyegongdan.dto.AssignRequest;
import com.example.qiyegongdan.dto.CommentRequest;
import com.example.qiyegongdan.dto.TicketCreateRequest;
import com.example.qiyegongdan.dto.TicketVO;
import com.example.qiyegongdan.entity.*;
import com.example.qiyegongdan.mapper.*;
import com.example.qiyegongdan.security.LoginUser;
import com.example.qiyegongdan.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 工单服务：创建、分配、状态流转、评论、附件、查询
 */
@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketMapper ticketMapper;
    private final TicketTypeMapper ticketTypeMapper;
    private final UserMapper userMapper;
    private final CustomFieldMapper customFieldMapper;
    private final TicketFieldValueMapper ticketFieldValueMapper;
    private final TicketCommentMapper ticketCommentMapper;
    private final TicketAttachmentMapper ticketAttachmentMapper;
    private final TicketStatusLogMapper ticketStatusLogMapper;
    private final TicketAssignMapper ticketAssignMapper;

    // ---------- 创建工单 ----------

    @Transactional
    public TicketVO create(TicketCreateRequest request) {
        LoginUser user = requireUser();
        TicketType type = ticketTypeMapper.selectById(request.getTypeId());
        if (type == null) {
            throw new BizException("工单类型不存在");
        }
        Ticket ticket = new Ticket();
        ticket.setTicketNo(generateTicketNo());
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setPriority(request.getPriority() == null ? "MEDIUM" : request.getPriority());
        ticket.setStatus("NEW");
        ticket.setTypeId(request.getTypeId());
        ticket.setGroupId(type.getGroupId());
        ticket.setCreatorId(user.getId());
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());
        ticketMapper.insert(ticket);

        // 保存自定义字段值
        if (request.getFieldValues() != null) {
            request.getFieldValues().forEach((key, value) -> {
                CustomField field = customFieldMapper.selectOne(new LambdaQueryWrapper<CustomField>()
                        .eq(CustomField::getTicketTypeId, request.getTypeId())
                        .eq(CustomField::getFieldKey, key));
                if (field != null) {
                    TicketFieldValue fv = new TicketFieldValue();
                    fv.setTicketId(ticket.getId());
                    fv.setFieldId(field.getId());
                    fv.setFieldKey(key);
                    fv.setFieldValue(value == null ? null : String.valueOf(value));
                    ticketFieldValueMapper.insert(fv);
                }
            });
        }

        logStatus(ticket.getId(), null, "NEW", user.getId(), "创建工单");
        return toVO(ticket, true, true);
    }

    private String generateTicketNo() {
        return "TK" + DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmss")
                + RandomUtil.randomNumbers(4);
    }

    // ---------- 分配 ----------

    @Transactional
    public TicketVO assign(Long ticketId, AssignRequest request) {
        LoginUser user = requireUser();
        Ticket ticket = ticketMapper.selectById(ticketId);
        if (ticket == null) {
            throw new BizException("工单不存在");
        }
        // 自动分配：最少负载算法
        if (Boolean.TRUE.equals(request.getAuto()) || request.getAssigneeId() == null) {
            Long assigneeId = autoAssign(ticket);
            ticket.setAssigneeId(assigneeId);
            saveAssign(ticketId, user.getId(), assigneeId, "AUTO", request.getRemark());
        } else {
            User assignee = userMapper.selectById(request.getAssigneeId());
            if (assignee == null || !"AGENT".equals(assignee.getRoleCode())) {
                throw new BizException("分配对象必须是启用状态的客服");
            }
            ticket.setAssigneeId(request.getAssigneeId());
            saveAssign(ticketId, user.getId(), request.getAssigneeId(), "MANUAL", request.getRemark());
        }
        // 分配后状态流转：NEW -> ASSIGNED
        if ("NEW".equals(ticket.getStatus())) {
            changeStatusInternal(ticket, "ASSIGNED", user.getId(), "分配工单");
        } else {
            ticket.setUpdatedAt(LocalDateTime.now());
            ticketMapper.updateById(ticket);
        }
        return toVO(ticket, true, false);
    }

    /**
     * 自动分配：优先分配给处理中工单数最少的激活客服
     */
    private Long autoAssign(Ticket ticket) {
        List<Long> agentIds = userMapper.selectIdsByRoleCode("AGENT");
        if (agentIds == null || agentIds.isEmpty()) {
            throw new BizException("暂无可分配的客服，请先在用户管理中创建客服账号");
        }
        Long best = null;
        Long min = Long.MAX_VALUE;
        for (Long id : agentIds) {
            Long count = userMapper.countActiveTickets(id);
            if (count < min) {
                min = count;
                best = id;
            }
        }
        if (best == null) {
            best = agentIds.get(ThreadLocalRandom.current().nextInt(agentIds.size()));
        }
        return best;
    }

    private void saveAssign(Long ticketId, Long operatorId, Long assigneeId, String mode, String remark) {
        TicketAssign ta = new TicketAssign();
        ta.setTicketId(ticketId);
        ta.setOperatorId(operatorId);
        ta.setAssigneeId(assigneeId);
        ta.setMode(mode);
        ta.setRemark(remark);
        ta.setCreatedAt(LocalDateTime.now());
        ticketAssignMapper.insert(ta);
    }

    // ---------- 状态流转 ----------

    @Transactional
    public TicketVO changeStatus(Long ticketId, String toStatus, String remark) {
        LoginUser user = requireUser();
        Ticket ticket = ticketMapper.selectById(ticketId);
        if (ticket == null) {
            throw new BizException("工单不存在");
        }
        // 校验状态流转合法性
        if (!isValidTransition(ticket.getStatus(), toStatus)) {
            throw new BizException("不允许从 " + ticket.getStatus() + " 流转到 " + toStatus);
        }
        changeStatusInternal(ticket, toStatus, user.getId(), remark);
        return toVO(ticket, true, false);
    }

    private void changeStatusInternal(Ticket ticket, String toStatus, Long operatorId, String remark) {
        String from = ticket.getStatus();
        ticket.setStatus(toStatus);
        switch (toStatus) {
            case "PROCESSING":
                if (ticket.getHandledAt() == null) {
                    ticket.setHandledAt(LocalDateTime.now());
                }
                break;
            case "RESOLVED":
                ticket.setResolvedAt(LocalDateTime.now());
                break;
            case "CLOSED":
                ticket.setClosedAt(LocalDateTime.now());
                if (ticket.getResolvedAt() == null) {
                    ticket.setResolvedAt(LocalDateTime.now());
                }
                break;
            default:
                break;
        }
        ticket.setUpdatedAt(LocalDateTime.now());
        ticketMapper.updateById(ticket);
        logStatus(ticket.getId(), from, toStatus, operatorId, remark);
    }

    private boolean isValidTransition(String from, String to) {
        return switch (from) {
            case "NEW" -> List.of("ASSIGNED", "REJECTED", "CLOSED").contains(to);
            case "ASSIGNED" -> List.of("PROCESSING", "REJECTED").contains(to);
            case "PROCESSING" -> List.of("RESOLVED", "REJECTED").contains(to);
            case "RESOLVED" -> List.of("CLOSED", "PROCESSING").contains(to);
            case "REJECTED" -> List.of("ASSIGNED", "PROCESSING", "CLOSED").contains(to);
            case "CLOSED" -> false;
            default -> false;
        };
    }

    private void logStatus(Long ticketId, String from, String to, Long operatorId, String remark) {
        TicketStatusLog log = new TicketStatusLog();
        log.setTicketId(ticketId);
        log.setFromStatus(from);
        log.setToStatus(to);
        log.setOperatorId(operatorId);
        log.setRemark(remark);
        log.setCreatedAt(LocalDateTime.now());
        ticketStatusLogMapper.insert(log);
    }

    // ---------- 评论 ----------

    public TicketComment addComment(Long ticketId, CommentRequest request) {
        LoginUser user = requireUser();
        TicketComment comment = new TicketComment();
        comment.setTicketId(ticketId);
        comment.setUserId(user.getId());
        comment.setContent(request.getContent());
        comment.setIsInternal(request.getIsInternal() == null ? 0 : request.getIsInternal());
        comment.setCreatedAt(LocalDateTime.now());
        ticketCommentMapper.insert(comment);
        return comment;
    }

    // ---------- 查询 ----------

    public Page<TicketVO> page(int pageNum, int pageSize, Map<String, Object> filters) {
        LoginUser user = requireUser();
        LambdaQueryWrapper<Ticket> wrapper = new LambdaQueryWrapper<>();
        if (filters != null) {
            Object status = filters.get("status");
            Object priority = filters.get("priority");
            Object typeId = filters.get("typeId");
            Object keyword = filters.get("keyword");
            Object assigneeId = filters.get("assigneeId");
            Object creatorId = filters.get("creatorId");
            if (status != null && StringUtils.hasText(status.toString())) {
                wrapper.eq(Ticket::getStatus, status);
            }
            if (priority != null && StringUtils.hasText(priority.toString())) {
                wrapper.eq(Ticket::getPriority, priority);
            }
            if (typeId != null && StringUtils.hasText(typeId.toString())) {
                wrapper.eq(Ticket::getTypeId, typeId);
            }
            if (keyword != null && StringUtils.hasText(keyword.toString())) {
                wrapper.and(w -> w.like(Ticket::getTitle, keyword).or().like(Ticket::getTicketNo, keyword));
            }
            if (assigneeId != null && StringUtils.hasText(assigneeId.toString())) {
                wrapper.eq(Ticket::getAssigneeId, assigneeId);
            }
            if (creatorId != null && StringUtils.hasText(creatorId.toString())) {
                wrapper.eq(Ticket::getCreatorId, creatorId);
            }
        }
        // 角色数据权限：客服只看自己被分配和创建的；普通用户只看自己创建的
        if ("AGENT".equals(user.getRoleCode())) {
            wrapper.and(w -> w.eq(Ticket::getAssigneeId, user.getId())
                    .or().eq(Ticket::getCreatorId, user.getId()));
        } else if (!"ADMIN".equals(user.getRoleCode())) {
            wrapper.eq(Ticket::getCreatorId, user.getId());
        }
        wrapper.orderByDesc(Ticket::getId);
        Page<Ticket> page = ticketMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        Page<TicketVO> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(page.getRecords().stream().map(t -> toVO(t, false, false)).toList());
        return result;
    }

    public TicketVO detail(Long ticketId) {
        Ticket ticket = ticketMapper.selectById(ticketId);
        if (ticket == null) {
            throw new BizException("工单不存在");
        }
        return toVO(ticket, true, true);
    }

    private TicketVO toVO(Ticket ticket, boolean withComments, boolean withAttachments) {
        TicketVO vo = new TicketVO();
        vo.setId(ticket.getId());
        vo.setTicketNo(ticket.getTicketNo());
        vo.setTitle(ticket.getTitle());
        vo.setDescription(ticket.getDescription());
        vo.setPriority(ticket.getPriority());
        vo.setStatus(ticket.getStatus());
        vo.setTypeId(ticket.getTypeId());
        vo.setCreatorId(ticket.getCreatorId());
        vo.setAssigneeId(ticket.getAssigneeId());
        vo.setCreatedAt(ticket.getCreatedAt());
        vo.setUpdatedAt(ticket.getUpdatedAt());
        vo.setResolvedAt(ticket.getResolvedAt());
        vo.setClosedAt(ticket.getClosedAt());

        TicketType type = ticketTypeMapper.selectById(ticket.getTypeId());
        if (type != null) {
            vo.setTypeName(type.getName());
        }
        User creator = userMapper.selectById(ticket.getCreatorId());
        vo.setCreatorName(creator == null ? null : creator.getName());
        if (ticket.getAssigneeId() != null) {
            User assignee = userMapper.selectById(ticket.getAssigneeId());
            vo.setAssigneeName(assignee == null ? null : assignee.getName());
        }

        // 自定义字段值
        List<TicketFieldValue> fvs = ticketFieldValueMapper.selectList(
                new LambdaQueryWrapper<TicketFieldValue>().eq(TicketFieldValue::getTicketId, ticket.getId()));
        Map<String, Object> fieldMap = new HashMap<>();
        fvs.forEach(fv -> fieldMap.put(fv.getFieldKey(), fv.getFieldValue()));
        vo.setFieldValues(fieldMap);

        if (withComments) {
            List<TicketComment> comments = ticketCommentMapper.selectList(
                    new LambdaQueryWrapper<TicketComment>().eq(TicketComment::getTicketId, ticket.getId())
                            .orderByAsc(TicketComment::getId));
            List<Object> commentList = new ArrayList<>();
            comments.forEach(c -> {
                User u = userMapper.selectById(c.getUserId());
                Map<String, Object> m = new HashMap<>();
                m.put("id", c.getId());
                m.put("content", c.getContent());
                m.put("isInternal", c.getIsInternal());
                m.put("userName", u == null ? null : u.getName());
                m.put("createdAt", c.getCreatedAt());
                commentList.add(m);
            });
            vo.setComments(commentList);
        }

        if (withAttachments) {
            List<TicketAttachment> attachments = ticketAttachmentMapper.selectList(
                    new LambdaQueryWrapper<TicketAttachment>().eq(TicketAttachment::getTicketId, ticket.getId()));
            List<Object> attList = new ArrayList<>();
            attachments.forEach(a -> {
                Map<String, Object> m = new HashMap<>();
                m.put("id", a.getId());
                m.put("fileName", a.getFileName());
                m.put("filePath", a.getFilePath());
                m.put("fileSize", a.getFileSize());
                m.put("fileType", a.getFileType());
                m.put("createdAt", a.getCreatedAt());
                attList.add(m);
            });
            vo.setAttachments(attList);
        }
        return vo;
    }

    private LoginUser requireUser() {
        LoginUser user = SecurityUtil.getCurrentUser();
        if (user == null) {
            throw new BizException(401, "未登录");
        }
        return user;
    }
}