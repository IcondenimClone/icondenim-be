package com.store.backend.common;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.store.backend.exception.AlreadyExistsException;
import com.store.backend.exception.NotCorrectException;
import com.store.backend.exception.NotFoundException;
import com.store.backend.exception.TooManyException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse> handleValidationException(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
    Map<String, Object> data = new HashMap<>();
    data.put("errors", errors);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Dữ liệu không hợp lệ", data));
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ApiResponse> handleBadCredentialsException(BadCredentialsException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Tài khoản hoặc mật khẩu không chính xác", null));
  }

  @ExceptionHandler(AlreadyExistsException.class)
  public ResponseEntity<ApiResponse> handleAlreadyExistsException(AlreadyExistsException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(ex.getMessage(), null));
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ApiResponse> handleNotFoundException(NotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(ex.getMessage(), null));
  }

  @ExceptionHandler(TooManyException.class)
  public ResponseEntity<ApiResponse> handleTooManyException(TooManyException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(ex.getMessage(), null));
  }

  @ExceptionHandler(NotCorrectException.class)
  public ResponseEntity<ApiResponse> handleNotCorrectException(NotCorrectException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(ex.getMessage(), null));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse> handleGenericException(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ApiResponse(ex.getMessage(), null));
  }
}
