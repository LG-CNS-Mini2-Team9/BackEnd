package com.team9.api_gateway.filter;

import org.springframework.cloud.gateway.server.mvc.common.Shortcut;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.ServerResponse;

public interface GatewayFilterFunctions {
    @Shortcut
    static HandlerFilterFunction<ServerResponse, ServerResponse> addAuthenticationHeader() {
        return HandlerFilterFunction.ofRequestProcessor(AuthenticationHeaderFilterFunction.addHeader());
    };
}
