package com.dheerxpay.merchant.service.impl;

import com.dheerxpay.common.enums.BusinessType;
import com.dheerxpay.common.enums.MerchantStatus;
import com.dheerxpay.common.enums.UserRoles;
import com.dheerxpay.common.exceptions.DuplicateResourceException;
import com.dheerxpay.merchant.dto.request.MerchantSignUpRequest;
import com.dheerxpay.merchant.dto.response.MerchantResponse;
import com.dheerxpay.merchant.entities.AppUser;
import com.dheerxpay.merchant.entities.Merchant;
import com.dheerxpay.merchant.mapper.MerchantMapper;
import com.dheerxpay.merchant.repository.AppUserRespository;
import com.dheerxpay.merchant.repository.MerchantRepository;
import com.dheerxpay.merchant.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private MerchantRepository merchantRepository;
    @Mock
    private AppUserRespository appUserRespository;
    @Mock
    private MerchantMapper merchantMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void signUpCreatesMerchantOwnerAndResponse() {
        MerchantSignUpRequest request = new MerchantSignUpRequest(
                "Example Store", BusinessType.PRIVATE_LIMITED,
                "Example Legal", "owner@example.com", "password123", "1234567890"
        );
        Merchant merchant = new Merchant();
        Merchant savedMerchant = new Merchant();
        MerchantResponse response = MerchantResponse.builder().email(request.email()).build();
        when(merchantRepository.existsByEmailIgnoreCase(request.email())).thenReturn(false);
        when(appUserRespository.findByEmailIgnoreCase(request.email())).thenReturn(java.util.Optional.empty());
        when(merchantMapper.toEntity(eq(request), eq(MerchantStatus.PENDING_KYC), any()))
                .thenReturn(merchant);
        when(merchantRepository.save(merchant)).thenReturn(savedMerchant);
        when(merchantMapper.toAppUser(request, savedMerchant, UserRoles.OWNER)).thenReturn(new AppUser());
        when(passwordEncoder.encode(request.password())).thenReturn("encoded-password");
        when(merchantMapper.toResponse(savedMerchant)).thenReturn(response);

        MerchantResponse actual = authService.signUp(request);

        assertSame(response, actual);
        verify(merchantMapper).toAppUser(request, savedMerchant, UserRoles.OWNER);
        org.mockito.ArgumentCaptor<AppUser> savedUser = org.mockito.ArgumentCaptor.forClass(AppUser.class);
        verify(appUserRespository).save(savedUser.capture());
        assertEquals("encoded-password", savedUser.getValue().getPasswordHash());
        assertEquals("owner@example.com", savedUser.getValue().getEmail());
    }

    @Test
    void signUpRejectsDuplicateEmailBeforeSaving() {
        MerchantSignUpRequest request = new MerchantSignUpRequest(
                "Example Store", BusinessType.PRIVATE_LIMITED,
                "Example Legal", "owner@example.com", "password123", "1234567890"
        );
        when(merchantRepository.existsByEmailIgnoreCase(request.email())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.signUp(request));

        verify(merchantMapper, never()).toEntity(any(), any(), any());
        verify(merchantRepository, never()).save(any());
        verify(appUserRespository, never()).save(any());
    }
}
