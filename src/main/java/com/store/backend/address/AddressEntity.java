package com.store.backend.address;

import com.store.backend.common.BaseEntity;
import com.store.backend.user.UserEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "addresses")
public class AddressEntity extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(nullable = false, length = 36)
  private String id;

  @Column(length = 50)
  private String firstName;

  @Column(length = 50)
  private String lastName;

  @Column(nullable = false, length = 11)
  private String phoneNumber;

  @Column(nullable = false, length = 255)
  private String address;

  @Column(nullable = false, length = 50)
  private String commune;

  @Column(nullable =  false, length = 50)
  private String district;

  @Column(nullable = false, length = 50)
  private String province;

  @Column(nullable = false)
  @Builder.Default
  private boolean defaultAddress = false;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private UserEntity user;
}
