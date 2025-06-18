package com.store.backend.address;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<AddressEntity, String> {
  long countByUserId(String userId);

  List<AddressEntity> findAllByUserId(String userId);
}
