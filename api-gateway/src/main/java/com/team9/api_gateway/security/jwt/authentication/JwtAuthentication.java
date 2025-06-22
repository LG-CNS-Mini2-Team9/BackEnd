package com.team9.api_gateway.security.jwt.authentication;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class JwtAuthentication extends AbstractAuthenticationToken {
    private final String token; //credential
    private final UserPrinciple principle;

    public JwtAuthentication( UserPrinciple principle, String token,
                             Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.token = token;
        this.principle = principle;
        this.setDetails(principle);
        setAuthenticated(true);
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public String getCredentials() {
        return token;
    }

    @Override
    public UserPrinciple getPrincipal() {
        return principle;
    }
}
