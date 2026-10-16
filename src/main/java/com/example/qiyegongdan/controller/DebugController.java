package com.example.qiyegongdan.controller;

import com.example.qiyegongdan.common.Result;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    private final ListableBeanFactory beanFactory;

    public DebugController(ListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @GetMapping("/beans")
    public Result<String> beans() {
        String[] names = beanFactory.getBeanDefinitionNames();
        String relevant = Arrays.stream(names)
                .filter(n -> {
                    String l = n.toLowerCase();
                    return l.contains("methodsecurity")
                            || l.contains("authorizationmanager")
                            || l.contains("interceptor")
                            || l.contains("aspectj")
                            || l.contains("aop");
                })
                .collect(Collectors.joining(", "));
        return Result.success("beans=[" + relevant + "] count=" + names.length);
    }

    @GetMapping("/whoami")
    public Result<String> whoami() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null) {
            return Result.success("NO_AUTH");
        }
        String auths = a.getAuthorities() == null
                ? "NULL"
                : a.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(","));
        return Result.success("name=" + a.getName() + " | auths=[" + auths + "] | cls=" + a.getClass().getName());
    }

    @GetMapping("/deny")
    @PreAuthorize("denyAll()")
    public Result<String> deny() {
        return Result.success("DENY_BYPASSED");
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> admin() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        String auths = a.getAuthorities() == null ? "NULL"
                : a.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        return Result.success("ADMIN_BODY auths=[" + auths + "]");
    }

    @GetMapping("/proxy")
    public Result<String> proxy() {
        return Result.success("self=" + getClass().getName()
                + " super=" + getClass().getSuperclass().getName());
    }
}