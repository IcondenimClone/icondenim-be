package com.store.backend.voucher;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VoucherRepository extends JpaRepository<VoucherEntity, String> {
  boolean existsByCodeIgnoreCase(String code);

  Optional<VoucherEntity> findByCodeIgnoreCase(String code);
}
