package com.team9.api_gateway.filter;

import com.team9.api_gateway.security.jwt.authentication.UserPrinciple;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.function.ServerRequest;

import java.util.function.Function;

public class AuthenticationHeaderFilterFunction {
    public static Function<ServerRequest, ServerRequest> addHeader() {
        return request -> {
            ServerRequest.Builder requestBuilder = ServerRequest.from(request);

            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserPrinciple userPrinciple) {
                requestBuilder.header("X-Auth-UserId", userPrinciple.getUserId());
            }

            return requestBuilder.build();
        };
    }
}
