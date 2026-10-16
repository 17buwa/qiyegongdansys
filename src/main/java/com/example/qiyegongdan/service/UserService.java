package com.example.qiyegongdan.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.qiyegongdan.common.BizException;
import com.example.qiyegongdan.dto.UserRequest;
import com.example.qiyegongdan.dto.UserVO;
import com.example.qiyegongdan.entity.User;
import com.example.qiyegongdan.mapper.PermissionMapper;
import com.example.qiyegongdan.mapper.UserMapper;
import com.example.qiyegongdan.security.LoginUser;
import com.example.qiyegongdan.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户服务
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final PermissionMapper permissionMapper;

    public Page<UserVO> page(int pageNum, int pageSize, String keyword, String roleCode) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(User::getUsername, keyword).or().like(User::getName, keyword));
        }
        if (StringUtils.hasText(roleCode)) {
            wrapper.eq(User::getRoleCode, roleCode);
        }
        wrapper.orderByDesc(User::getId);
        Page<User> page = userMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        Page<UserVO> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(page.getRecords().stream().map(UserVO::from).toList());
        return result;
    }

    public UserVO create(UserRequest request) {
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));
        if (count > 0) {
            throw new BizException("用户名已存在");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(
                StringUtils.hasText(request.getPassword()) ? request.getPassword() : "123456"));
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRoleCode(request.getRoleCode());
        user.setStatus(request.getStatus() == null ? 1 : request.getStatus());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(user);
        return UserVO.from(user);
    }

    public UserVO update(Long id, UserRequest request) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRoleCode(request.getRoleCode());
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
        return UserVO.from(user);
    }

    public void delete(Long id) {
        LoginUser current = SecurityUtil.getCurrentUser();
        if (current != null && current.getId().equals(id)) {
            throw new BizException("不能删除当前登录用户");
        }
        userMapper.deleteById(id);
    }

    public Map<String, Object> profile() {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        List<String> perms = permissionMapper.selectByRoleCode(user.getRoleCode())
                .stream().map(p -> p.getPermCode()).toList();
        return Map.of("user", UserVO.from(user), "permissions", perms);
    }
}