package com.dheerxpay.vault.repository;

import com.dheerxpay.vault.entities.CardToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CardTokenRepository extends JpaRepository<CardToken, UUID> {
}
