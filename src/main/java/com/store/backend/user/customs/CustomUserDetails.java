package com.store.backend.user.customs;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.store.backend.user.UserEntity;
import com.store.backend.user.enums.UserRole;

import lombok.Getter;

@Getter
public class CustomUserDetails implements UserDetails {
  private final UserEntity user;

  public CustomUserDetails(UserEntity user) {
    this.user = user;
  }

  public String getId() {
    return user.getId();
  }

  public String getEmail() {
    return user.getEmail();
  }

  public String getFirstName() {
    return user.getFirstName();
  }

  public String getLastName() {
    return user.getLastName();
  }

  public UserRole getRole() {
    return user.getRole();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singleton(() -> "ROLE_" + user.getRole().name());
  }

  @Override
  public String getPassword() {
    return user.getPassword();
  }

  @Override
  public String getUsername() {
    return user.getUsername();
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

  @Override
  public boolean isEnabled() {
    return true;
  }
}
