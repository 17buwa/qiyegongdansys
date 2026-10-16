package com.example.qiyegongdan.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.qiyegongdan.entity.User;
import com.example.qiyegongdan.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 启动初始化：如果默认用户不存在则自动创建（默认密码 123456）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createIfAbsent("admin", "系统管理员", "ADMIN");
        createIfAbsent("agent01", "客服小张", "AGENT");
        createIfAbsent("agent02", "客服小李", "AGENT");
        createIfAbsent("user01", "普通用户", "CUSTOMER");
        log.info("默认用户初始化完成（默认密码 123456）");
    }

    private void createIfAbsent(String username, String name, String roleCode) {
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
        if (count == 0) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode("123456"));
            user.setName(name);
            user.setRoleCode(roleCode);
            user.setStatus(1);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            userMapper.insert(user);
        }
    }
}