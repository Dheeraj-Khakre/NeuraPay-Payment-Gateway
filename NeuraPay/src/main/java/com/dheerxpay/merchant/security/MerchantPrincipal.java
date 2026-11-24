package com.dheerxpay.merchant.security;

import com.dheerxpay.common.enums.MerchantStatus;
import com.dheerxpay.common.enums.UserRoles;
import com.dheerxpay.merchant.entities.AppUser;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public record MerchantPrincipal(
        UUID userId,
        UUID merchantId,
        String email,
        String passwordHash,
        UserRoles role,
        boolean enabled
) implements UserDetails {

    public static MerchantPrincipal current() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof MerchantPrincipal principal)) {
            throw new AuthenticationCredentialsNotFoundException("Authenticated merchant principal is required");
        }
        return principal;
    }

    public static MerchantPrincipal from(AppUser user) {
        MerchantStatus status = user.getMerchant().getStatus();
        boolean enabled = status != MerchantStatus.SUSPENDED
                && status != MerchantStatus.REJECTED
                && status != MerchantStatus.CLOSED;
        return new MerchantPrincipal(
                user.getId(),
                user.getMerchant().getId(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getRole(),
                enabled
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
