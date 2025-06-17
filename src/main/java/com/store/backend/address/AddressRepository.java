package com.store.backend.address;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<AddressEntity, String> {
  List<AddressEntity> findByUserId(String userId);
}
