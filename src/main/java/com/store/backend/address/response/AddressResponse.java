package com.store.backend.address.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressResponse {
  private String id;
  private String firstName;
  private String lastName;
  private String phoneNumber;
  private String address;
  private String commune;
  private String district;
  private String province;
  private boolean defaultAddress;
}
