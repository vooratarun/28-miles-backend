package com.quodex._miles.service;

import com.quodex._miles.io.RedisInsertRequest;
import com.quodex._miles.io.RedisInsertResponse;
import com.quodex._miles.io.RedisValueResponse;

public interface RedisService {

    RedisInsertResponse insert(RedisInsertRequest request);

    RedisValueResponse getByKey(String key);
}
