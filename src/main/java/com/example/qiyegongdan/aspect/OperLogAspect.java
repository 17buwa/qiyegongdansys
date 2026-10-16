package com.example.qiyegongdan.aspect;

import com.example.qiyegongdan.annotation.OperLog;
import com.example.qiyegongdan.entity.OperationLog;
import com.example.qiyegongdan.mapper.OperationLogMapper;
import com.example.qiyegongdan.security.LoginUser;
import com.example.qiyegongdan.security.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 操作日志切面：记录带 @OperLog 注解方法的调用
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperLogAspect {

    private final OperationLogMapper operationLogMapper;

    @Pointcut("@annotation(operLog)")
    public void pointcut(OperLog operLog) {
    }

    @AfterReturning(pointcut = "pointcut(operLog)", argNames = "joinPoint,operLog")
    public void saveLog(JoinPoint joinPoint, OperLog operLog) {
        try {
            OperationLog record = new OperationLog();
            LoginUser user = SecurityUtil.getCurrentUser();
            if (user != null) {
                record.setUserId(user.getId());
                record.setUsername(user.getUsername());
            }
            record.setModule(operLog.module());
            record.setAction(operLog.action());
            record.setDetail(buildDetail(joinPoint));
            record.setIp(getIp());
            record.setCreatedAt(LocalDateTime.now());
            operationLogMapper.insert(record);
        } catch (Exception e) {
            log.error("记录操作日志失败", e);
        }
    }

    private String buildDetail(JoinPoint joinPoint) {
        String method = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        // 只保留前若干参数，避免日志过长
        String paramStr = Arrays.stream(args).limit(3).map(String::valueOf).toList().toString();
        return method + " " + paramStr;
    }

    private String getIp() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                String ip = request.getHeader("X-Forwarded-For");
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getRemoteAddr();
                }
                return ip;
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}