package com.team9.api_gateway.security.jwt.authentication;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.util.Objects;

@Getter
@RequiredArgsConstructor
public class UserPrinciple implements Principal {
    private final String userId;

    public boolean hasName() {
        return userId != null;
    }

    public boolean hasMandatory() {
        return userId != null;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String getName() {
        return userId;
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) return true;
        if (another == null) return false;
        if (!getClass().isAssignableFrom(another.getClass())) return false;

        UserPrinciple principle = (UserPrinciple) another;

        return Objects.equals(userId, principle.getUserId());
    }

    @Override
    public int hashCode() {
        return userId != null ? userId.hashCode() : 0;
    }
}
