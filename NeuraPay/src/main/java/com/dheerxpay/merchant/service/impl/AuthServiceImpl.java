package com.dheerxpay.merchant.service.impl;

import com.dheerxpay.common.enums.MerchantStatus;
import com.dheerxpay.common.enums.UserRoles;
import com.dheerxpay.common.exceptions.DuplicateResourceException;
import com.dheerxpay.common.utility.RandomizerUtil;
import com.dheerxpay.merchant.dto.request.LoginRequest;
import com.dheerxpay.merchant.dto.request.MerchantSignUpRequest;
import com.dheerxpay.merchant.dto.response.AuthResponse;
import com.dheerxpay.merchant.dto.response.MerchantResponse;
import com.dheerxpay.merchant.entities.Merchant;
import com.dheerxpay.merchant.mapper.MerchantMapper;
import com.dheerxpay.merchant.repository.AppUserRespository;
import com.dheerxpay.merchant.repository.MerchantRepository;
import com.dheerxpay.merchant.security.JwtService;
import com.dheerxpay.merchant.security.MerchantPrincipal;
import com.dheerxpay.merchant.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl  implements AuthService {

    private final MerchantRepository merchantRepository;
    private final AppUserRespository appUserRespository;
    private final MerchantMapper merchantMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    @Override
    @Transactional
    public MerchantResponse signUp(MerchantSignUpRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (merchantRepository.existsByEmailIgnoreCase(email)
                || appUserRespository.findByEmailIgnoreCase(email).isPresent()) {
            throw new DuplicateResourceException("DUPLICATE_MERCHANT_EMAIL","merchant has already exists for given email:"+request.email());
        }
        Merchant merchant = merchantMapper.toEntity(
                request,
                MerchantStatus.PENDING_KYC,
                RandomizerUtil.generate()
        );
        merchant.setEmail(email);
        Merchant savedMerchant = merchantRepository.save(merchant);

        var user = merchantMapper.toAppUser(request, savedMerchant, UserRoles.OWNER);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        appUserRespository.save(user);
        return merchantMapper.toResponse(savedMerchant);

    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email().trim().toLowerCase(Locale.ROOT),
                        request.password()
                )
        );
        MerchantPrincipal principal = (MerchantPrincipal) authentication.getPrincipal();
        assert principal != null;
        return jwtService.createAuthResponse(principal);
    }
}
