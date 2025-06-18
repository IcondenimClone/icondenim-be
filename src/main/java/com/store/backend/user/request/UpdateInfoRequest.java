package com.store.backend.user.request;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateInfoRequest {
  @Size(max = 50, message = "Họ không vượt quá 50 ký tự")
  private String firstName;

  @Size(max = 50, message = "Tên không vượt quá 50 ký tự")
  private String lastName;

  @Past(message = "Ngày sinh phải ở quá khứ")
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate dob;
}
