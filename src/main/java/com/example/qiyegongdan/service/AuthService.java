package com.example.qiyegongdan.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.qiyegongdan.common.BizException;
import com.example.qiyegongdan.dto.LoginRequest;
import com.example.qiyegongdan.dto.UserVO;
import com.example.qiyegongdan.entity.User;
import com.example.qiyegongdan.mapper.UserMapper;
import com.example.qiyegongdan.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证服务
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public Map<String, Object> login(LoginRequest request) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BizException("用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            throw new BizException("账号已被禁用");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRoleCode());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", UserVO.from(user));
        return result;
    }
}