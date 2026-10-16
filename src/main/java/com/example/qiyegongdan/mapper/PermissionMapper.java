package com.example.qiyegongdan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.qiyegongdan.entity.Permission;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface PermissionMapper extends BaseMapper<Permission> {

    @Select("SELECT p.* FROM permissions p INNER JOIN role_permission rp ON p.id = rp.permission_id " +
            "INNER JOIN roles r ON r.id = rp.role_id WHERE r.role_code = #{roleCode}")
    List<Permission> selectByRoleCode(String roleCode);
}