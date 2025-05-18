package com.store.backend.user.customs;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.store.backend.user.UserEntity;
import com.store.backend.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
  
  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) {
    UserEntity user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user"));
    return new CustomUserDetails(user);
  }
}
