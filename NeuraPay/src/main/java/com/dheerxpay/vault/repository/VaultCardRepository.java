package com.dheerxpay.vault.repository;

import com.dheerxpay.vault.entities.VaultCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VaultCardRepository extends JpaRepository<VaultCard, UUID> {
}
