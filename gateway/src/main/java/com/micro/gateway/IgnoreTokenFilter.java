package com.micro.gateway;

import com.micro.gateway.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

@Component
@Slf4j
public class IgnoreTokenFilter extends AbstractGatewayFilterFactory<IgnoreTokenFilter.Config> {

    @Autowired
    private JwtUtil jwtUtil;

    private List<String> ignoredUris;

    public IgnoreTokenFilter() {
        super(Config.class);
        IgnoreInit();
    }

    private void IgnoreInit() {
        this.ignoredUris = asList("/user/login","/user/register","/user/info","/user/logout","/swagger-ui/**","/swagger-resources/**","/swagger-ui.html/**");
    }

    @Override
    public GatewayFilter apply(Config config) {
        log.debug("Ignoring these URIs: {}", this.ignoredUris);
        return (exchange, chain) -> {
            // 获取请求的URI
            String requestUri = exchange.getRequest().getURI().getPath();
            log.debug("Request URI: {}", requestUri);
            // 判断是否是需要放行的API，比如/user/register和/user/login
            if (this.ignoredUris.contains(requestUri)) {
                log.debug("letting go...");
                // 放行
                return chain.filter(exchange);
            }

            // 非放行的API进行Token验证
            String token = exchange.getRequest().getHeaders().getFirst("X-Token");
            log.debug("Token: {}", token);

            if (token != null) {
                // 进行Token验证逻辑
                if (isValidToken(token)) {
                    // 如果Token有效，继续请求链
                    return chain.filter(exchange);
                } else {
                    // 如果Token无效，返回未授权的响应
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }
            } else {
                // 没有X-Token头，返回未授权的响应
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        };
    }

    private Boolean isValidToken(String token) {
        if (token != null) {
            try {
                jwtUtil.parseToken(token);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false; // 拦截
    }

    public static class Config {
        private List<String> ignoredUris = new ArrayList<>();

        public List<String> getIgnoredUris() {
            return ignoredUris;
        }

        public void setIgnoredUris(List<String> ignoredUris) {
            this.ignoredUris = ignoredUris;
        }
    }

}