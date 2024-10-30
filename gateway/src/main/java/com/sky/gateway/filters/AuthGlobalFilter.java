package com.sky.gateway.filters;

import com.sky.exception.UnauthorizedException;
import com.sky.gateway.cofig.AuthProperties;
import com.sky.properties.JwtProperties;
import com.sky.constant.JwtClaimsConstant;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final AuthProperties authProperties;
    private final JwtProperties jwtProperties;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // 判断是否需要做登录拦截
        if (isExclude(request.getPath().toString())) {
            return chain.filter(exchange);
        }

        // 获取用户和管理员的Token
        String userToken = request.getHeaders().getFirst(jwtProperties.getUserTokenName());
        String adminToken = request.getHeaders().getFirst(jwtProperties.getAdminTokenName());

        try {
            if (userToken != null) {
                // 校验用户Token
                Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), userToken);
                Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
                log.info("当前用户id：{}", userId);
                // 传递用户信息
                ServerWebExchange modifiedExchange = exchange.mutate()
                        .request(builder -> {
                            builder.header("user-info", userId.toString());
                        })
                        .build();
                return chain.filter(modifiedExchange);
            } else if (adminToken != null) {
                // 校验管理员Token
                Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), adminToken);
                Long empId = Long.valueOf(claims.get(JwtClaimsConstant.EMP_ID).toString());
                log.info("当前员工id：{}", empId);
                // 传递管理员信息
                ServerWebExchange modifiedExchange = exchange.mutate()
                        .request(builder -> {
                            builder.header("admin-info", empId.toString());
                        })
                        .build();
                return chain.filter(modifiedExchange);
            } else {
                return unauthorizedResponse(exchange);
            }
        } catch (UnauthorizedException e) {
            return unauthorizedResponse(exchange);
        }
    }

    private boolean isExclude(String path) {
        List<String> excludePaths = authProperties.getExcludePaths();
        for (String pathPattern : excludePaths) {
            if (antPathMatcher.match(pathPattern, path)) {
                return true;
            }
        }
        return false;
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}

