package com.store.backend.cache;

import java.util.concurrent.TimeUnit;

public interface RedisService {
  String setKey(String originKey, String keyType);

  void saveString(String key, String content, long timeout, TimeUnit unit);

  String getString(String key);

  void updateString(String key, String content);

  void deleteString(String key);

  void saveObject(String key, Object object, long timeout, TimeUnit unit);

  Object getObject(String key);

  void updateObject(String key, Object object);

  void deleteObject(String key);
}
