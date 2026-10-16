package com.example.qiyegongdan.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * JwtUtil 单元测试（不依赖数据库，可独立运行）
 */
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() throws Exception {
        jwtUtil = new JwtUtil();
        // 通过反射注入字段，模拟 @Value 注入
        setField("secret", "qiyegongdan-jwt-secret-key-please-change-in-production-0123456789");
        setField("expiration", 86400000L);
    }

    private void setField(String name, Object value) throws Exception {
        Field field = JwtUtil.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(jwtUtil, value);
    }

    @Test
    void generateAndParseToken() {
        String token = jwtUtil.generateToken(1L, "admin", "ADMIN");
        assertNotNull(token);
        assertNotNull(token.split("\\.").length == 3 ? token : null);

        Claims claims = jwtUtil.parseToken(token);
        assertEquals("admin", claims.getSubject());
        assertEquals(1, claims.get("userId", Integer.class).intValue());
        assertEquals("ADMIN", claims.get("roleCode", String.class));
    }

    @Test
    void tokenIsTamperedRaiseException() {
        String token = jwtUtil.generateToken(1L, "admin", "ADMIN");
        String tampered = token.substring(0, token.length() - 2) + "xx";
        assertThrows(Exception.class, () -> jwtUtil.parseToken(tampered));
    }

    @Test
    void tokenFormatIsStandardJwt() {
        String token = jwtUtil.generateToken(1L, "admin", "ADMIN");
        // 标准 JWT 为三部分：header.payload.signature
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length);
    }

    @Test
    void tokenExpirationIsInTheFuture() {
        String token = jwtUtil.generateToken(1L, "admin", "ADMIN");
        Claims claims = jwtUtil.parseToken(token);
        assertNotNull(claims.getExpiration());
        org.junit.jupiter.api.Assertions.assertTrue(
                claims.getExpiration().getTime() > System.currentTimeMillis());
    }
}