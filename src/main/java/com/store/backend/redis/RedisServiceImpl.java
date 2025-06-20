package com.store.backend.redis;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.store.backend.exception.NotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisServiceImpl implements RedisService {
  private final RedisTemplate<String, Object> objectRedisTemplate;
  private final StringRedisTemplate stringRedisTemplate;

  @Override
  public String setKey(String originKey, String keyType) {
    return new StringBuilder().append("icondenim").append(keyType).append(originKey).toString();
  }

  @Override
  public void saveString(String key, String content, long timeout, TimeUnit unit) {
    stringRedisTemplate.opsForValue().set(key, content, timeout, unit);
    log.info("Đã lưu key '{}' vào Redis với TTL {} {}", key, timeout, unit);
  }

  @Override
  public String getString(String key) {
    if (!checkKeyExists(key, true)) {
      throw new NotFoundException("Không tìm thấy dữ liệu trong bộ nhớ");
    }
    return stringRedisTemplate.opsForValue().get(key);
  }

  @Override
  public void updateString(String key, String content) {
    if (!checkKeyExists(key, true)) {
      throw new NotFoundException("Không tìm thấy dữ liệu trong bộ nhớ");
    }
    stringRedisTemplate.opsForValue().set(key, content);
  }

  @Override
  public void deleteString(String key) {
    if (!checkKeyExists(key, true)) {
      throw new NotFoundException("Không tìm thấy dữ liệu trong bộ nhớ");
    }
    stringRedisTemplate.delete(key);
  }

  @Override
  public void saveObject(String key, Object object, long timeout, TimeUnit unit) {
    objectRedisTemplate.opsForValue().set(key, object, timeout, unit);
    log.info("Đã lưu key '{}' vào Redis với TTL {} {}", key, timeout, unit);
  }

  @Override
  public Object getObject(String key) {
    if (!checkKeyExists(key, false)) {
      throw new NotFoundException("Không tìm thấy dữ liệu trong bộ nhớ");
    }
    return objectRedisTemplate.opsForValue().get(key);
  }

  @Override
  public void updateObject(String key, Object object) {
    if (!checkKeyExists(key, false)) {
      throw new NotFoundException("Không tìm thấy dữ liệu trong bộ nhớ");
    }
    objectRedisTemplate.opsForValue().set(key, object);
  }

  @Override
  public void deleteObject(String key) {
    if (!checkKeyExists(key, false)) {
      throw new NotFoundException("Không tìm thấy dữ liệu trong bộ nhớ");
    }
    objectRedisTemplate.delete(key);
  }

  private boolean checkKeyExists(String key, boolean isString) {
    return isString ? stringRedisTemplate.hasKey(key) : objectRedisTemplate.hasKey(key);
  }
}
