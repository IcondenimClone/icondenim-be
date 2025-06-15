package com.store.backend.user.customs;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.store.backend.user.UserEntity;
import com.store.backend.user.UserRepository;
import com.store.backend.user.enums.UserRole;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserEntity user = Optional.ofNullable(userRepository.findByUsername(username))
        .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user có username là" + username));
    return User.builder().username(user.getUsername()).password(user.getPassword())
        .authorities(getAuthorities(user.getRole())).accountExpired(false).accountLocked(false)
        .credentialsExpired(false).build();
  }

  private Collection<? extends GrantedAuthority> getAuthorities(UserRole role) {
    return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
  }
}
