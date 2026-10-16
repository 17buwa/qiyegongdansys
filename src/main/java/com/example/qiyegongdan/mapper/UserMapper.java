package com.example.qiyegongdan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.qiyegongdan.entity.User;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户 Mapper
 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * 获取指定角色的用户id列表
     */
    @Select("SELECT id FROM users WHERE role_code = #{roleCode} AND status = 1 AND deleted = 0")
    List<Long> selectIdsByRoleCode(String roleCode);

    /**
     * 统计某客服当前处理中且未关闭的工单数
     */
    @Select("SELECT COUNT(*) FROM ticket WHERE assignee_id = #{userId} AND status IN ('ASSIGNED','PROCESSING') AND deleted = 0")
    Long countActiveTickets(Long userId);
}