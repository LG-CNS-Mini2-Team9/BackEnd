package com.team9.api_gateway.security.jwt.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(value = "jwt")
@Getter @Setter
public class JwtConfigProperties {
    private String header;
    private String secretKey;
    private Integer expiresIn;
    private Integer mobileExpiresIn;
    private Integer tableExpiresIn;
}
