package com.jseed.redis.starter.util;

import cn.hutool.core.collection.IterUtil;
import com.jseed.core.starter.utils.ObjectConverter;
import lombok.AllArgsConstructor;
import org.redisson.api.*;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Redis utilities
 *
 * @author cyrus
 * @version 3.1.0
 */
@AllArgsConstructor
@SuppressWarnings(value = {"rawtypes"})
public class RedisUtil {
    private final RedissonClient redissonClient;
    private final RedisTemplate<String, Object> redisTemplate;
    private final TenantEnvUtil tenantEnvUtil;

    /**
     * Rate limiting
     *
     * @param key          rate limiter key
     * @param rateType     rate limiting mode
     * @param rate         permit rate
     * @param rateInterval rate interval
     * @return -1 if a permit could not be acquired
     */
    public long rateLimiter(String key, RateType rateType, int rate, int rateInterval) {
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(tenantEnvUtil.envJoin(key));
        rateLimiter.trySetRate(rateType, rate, rateInterval, RateIntervalUnit.SECONDS);
        if (rateLimiter.tryAcquire()) {
            return rateLimiter.availablePermits();
        } else {
            return -1L;
        }
    }

    /**
     * Gets the Redis client.
     */
    public RedissonClient getClient() {
        return redissonClient;
    }

    /**
     * Publishes a message to a channel.
     *
     * @param channelKey channel key
     * @param msg        message to publish
     * @param consumer   callback invoked after publishing
     */
    public <T> void publish(String channelKey, T msg, Consumer<T> consumer) {
        RTopic topic = redissonClient.getTopic(tenantEnvUtil.envJoin(channelKey));
        topic.publish(msg);
        consumer.accept(msg);
    }

    public <T> void publish(String channelKey, T msg) {
        RTopic topic = redissonClient.getTopic(tenantEnvUtil.envJoin(channelKey));
        topic.publish(msg);
    }

    /**
     * Subscribes to messages on a channel.
     *
     * @param channelKey channel key
     * @param clazz      message type
     * @param consumer   callback invoked after publishing
     */
    public <T> void subscribe(String channelKey, Class<T> clazz, Consumer<T> consumer) {
        RTopic topic = redissonClient.getTopic(tenantEnvUtil.envJoin(channelKey));
        topic.addListener(clazz, (channel, msg) -> consumer.accept(msg));
    }

    /**
     * Caches a value such as an Integer, a String, or an entity.
     *
     * @param key   cache key
     * @param value value to cache
     */
    public <T> void setCacheObject(String key, final T value) {
        setCacheObject(key, value, false);
    }

    /**
     * Caches a value while preserving the current TTL.
     *
     * @param key       cache key
     * @param value     value to cache
     * @param isSaveTtl whether to preserve the remaining TTL after updating the value
     * @since Redis 6 supports setAndKeepTTL. The fallback supports Redis 5.
     */
    public <T> void setCacheObject(String key, final T value, final boolean isSaveTtl) {
        RBucket<Object> bucket = redissonClient.getBucket(tenantEnvUtil.envJoin(key));
        if (isSaveTtl) {
            try {
                bucket.setAndKeepTTL(value);
            } catch (Exception e) {
                long timeToLive = bucket.remainTimeToLive();
                // Set the expiration only if the remaining TTL is positive.
                if (timeToLive > 0) {
                    bucket.set(value);
                    bucket.expire(Duration.ofMillis(timeToLive));
                }
            }
        } else {
            bucket.set(value);
        }
    }

    /**
     * Caches a value such as an Integer, a String, or an entity.
     *
     * @param key      cache key
     * @param value    value to cache
     * @param duration time to live
     */
    public <T> void setCacheObject(String key, final T value, final Duration duration) {
        RBucket<T> result = redissonClient.getBucket(tenantEnvUtil.envJoin(key));
        result.set(value);
        result.expire(duration);
    }

    /**
     * Registers an object listener.
     * <p>
     * Enable Redis keyspace notifications for key listeners.
     *
     * @param key      cache key
     * @param listener listener to register
     */
    public <T> void addObjectListener(String key, final ObjectListener listener) {
        RBucket<T> result = redissonClient.getBucket(tenantEnvUtil.envJoin(key));
        result.addListener(listener);
    }

    /**
     * Sets the expiration.
     *
     * @param key     Redis key
     * @param timeout time to live
     * @return true if the expiration was set successfully, false otherwise
     */
    public boolean expire(final String key, final long timeout) {
        return expire(key, Duration.ofSeconds(timeout));
    }

    /**
     * Sets the expiration.
     *
     * @param key      Redis key
     * @param duration time to live
     * @return true if the expiration was set successfully, false otherwise
     */
    public boolean expire(String key, final Duration duration) {
        RBucket rBucket = redissonClient.getBucket(tenantEnvUtil.envJoin(key));
        return rBucket.expire(duration);
    }

    /**
     * Gets a cached value.
     *
     * @param key cache key
     * @return value stored under the key
     */
    public <T> T getCacheObject(String key) {
        RBucket<T> rBucket = redissonClient.getBucket(tenantEnvUtil.envJoin(key));
        return rBucket.get();
    }

    /**
     * Gets the remaining time to live for the key.
     *
     * @param key cache key
     * @return remaining time to live
     */
    public <T> long getTimeToLive(String key) {
        RBucket<T> rBucket = redissonClient.getBucket(tenantEnvUtil.envJoin(key));
        return rBucket.remainTimeToLive();
    }

    /**
     * Deletes one cached object.
     *
     * @param key cache key
     */
    public boolean deleteObject(String key) {
        return redissonClient.getBucket(tenantEnvUtil.envJoin(key)).delete();
    }

    /**
     * Deletes a collection of cached objects.
     *
     * @param collection keys to delete
     */
    public void deleteObject(Collection<String> collection) {
        RBatch batch = redissonClient.createBatch();
        collection.forEach(t -> batch.getBucket(tenantEnvUtil.envJoin(t)).deleteAsync());
        batch.execute();
    }

    /**
     * Caches a list.
     *
     * @param key      cache key
     * @param dataList list to cache
     * @return cached object
     */
    public <T> boolean setCacheList(String key, final List<T> dataList) {
        RList<T> rList = redissonClient.getList(tenantEnvUtil.envJoin(key));
        return rList.addAll(dataList);
    }

    /**
     * Registers a list listener.
     * <p>
     * Enable Redis keyspace notifications for key listeners.
     *
     * @param key      cache key
     * @param listener listener to register
     */
    public <T> void addListListener(final String key, final ObjectListener listener) {
        RList<T> rList = redissonClient.getList(tenantEnvUtil.envJoin(key));
        rList.addListener(listener);
    }

    /**
     * Gets a cached list.
     *
     * @param key cache key
     * @return value stored under the key
     */
    public <T> List<T> getCacheList(final String key) {
        RList<T> rList = redissonClient.getList(tenantEnvUtil.envJoin(key));
        return rList.readAll();
    }

    /**
     * Caches a set.
     *
     * @param key     cache key
     * @param dataSet set to cache
     * @return cache object
     */
    public <T> boolean setCacheSet(final String key, final Set<T> dataSet) {
        RSet<T> rSet = redissonClient.getSet(tenantEnvUtil.envJoin(key));
        return rSet.addAll(dataSet);
    }

    /**
     * Registers a set listener.
     * <p>
     * Enable Redis keyspace notifications for key listeners.
     *
     * @param key      cache key
     * @param listener listener to register
     */
    public <T> void addSetListener(final String key, final ObjectListener listener) {
        RSet<T> rSet = redissonClient.getSet(tenantEnvUtil.envJoin(key));
        rSet.addListener(listener);
    }

    /**
     * Gets a cached set.
     *
     * @param key cache key
     * @return cached set
     */
    public <T> Set<T> getCacheSet(final String key) {
        RSet<T> rSet = redissonClient.getSet(tenantEnvUtil.envJoin(key));
        return rSet.readAll();
    }

    /**
     * Caches a map.
     *
     * @param key     cache key
     * @param dataMap map to cache
     */
    public <T> void setCacheMap(final String key, final Map<String, T> dataMap) {
        if (dataMap != null) {
            RMap<String, T> rMap = redissonClient.getMap(tenantEnvUtil.envJoin(key));
            rMap.putAll(dataMap);
        }
    }

    /**
     * Registers a map listener.
     * <p>
     * Enable Redis keyspace notifications for key listeners.
     *
     * @param key      cache key
     * @param listener listener to register
     */
    public <T> void addMapListener(final String key, final ObjectListener listener) {
        RMap<String, T> rMap = redissonClient.getMap(tenantEnvUtil.envJoin(key));
        rMap.addListener(listener);
    }

    /**
     * Gets a cached map.
     *
     * @param key cache key
     * @return cached map
     */
    public <T> Map<String, T> getCacheMap(final String key) {
        RMap<String, T> rMap = redissonClient.getMap(tenantEnvUtil.envJoin(key));
        return rMap.getAll(rMap.keySet());
    }

    /**
     * Writes a value to a Redis hash.
     *
     * @param key   Redis key
     * @param hKey  hash field
     * @param value value to wrap
     */
    public <T> void setCacheMapValue(final String key, final String hKey, final T value) {
        RMap<String, T> rMap = redissonClient.getMap(tenantEnvUtil.envJoin(key));
        rMap.put(hKey, value);
    }

    /**
     * Reads a value from a Redis hash.
     *
     * @param key  Redis key
     * @param hKey hash field
     * @return value of the hash field
     */
    public <T> T getCacheMapValue(final String key, final String hKey) {
        RMap<String, T> rMap = redissonClient.getMap(tenantEnvUtil.envJoin(key));
        return rMap.get(hKey);
    }

    /**
     * Deletes a value from a Redis hash.
     *
     * @param key  Redis key
     * @param hKey hash field
     * @return value of the hash field
     */
    public <T> T delCacheMapValue(final String key, final String hKey) {
        RMap<String, T> rMap = redissonClient.getMap(tenantEnvUtil.envJoin(key));
        return rMap.remove(hKey);
    }

    /**
     * Gets values for multiple fields in a Redis hash.
     *
     * @param key   Redis key
     * @param hKeys hash fields
     * @return matching hash entries
     */
    public <K, V> Map<K, V> getMultiCacheMapValue(final String key, final Set<K> hKeys) {
        RMap<K, V> rMap = redissonClient.getMap(tenantEnvUtil.envJoin(key));
        return rMap.getAll(hKeys);
    }

    /**
     * Sets an atomic value.
     *
     * @param key   Redis key
     * @param value value to wrap
     */
    public void setAtomicValue(String key, long value) {
        RAtomicLong atomic = redissonClient.getAtomicLong(tenantEnvUtil.envJoin(key));
        atomic.set(value);
    }

    /**
     * Gets the atomic value.
     *
     * @param key Redis key
     * @return current value
     */
    public long getAtomicValue(String key) {
        RAtomicLong atomic = redissonClient.getAtomicLong(tenantEnvUtil.envJoin(key));
        return atomic.get();
    }

    /**
     * Increments the atomic value.
     *
     * @param key Redis key
     * @return current value
     */
    public long incrAtomicValue(String key) {
        RAtomicLong atomic = redissonClient.getAtomicLong(tenantEnvUtil.envJoin(key));
        return atomic.incrementAndGet();
    }

    /**
     * Decrements the atomic value.
     *
     * @param key Redis key
     * @return current value
     */
    public long decrAtomicValue(String key) {
        RAtomicLong atomic = redissonClient.getAtomicLong(tenantEnvUtil.envJoin(key));
        return atomic.decrementAndGet();
    }

    /**
     * Lists cached keys matching a pattern.
     *
     * @param pattern key pattern
     * @return matching keys
     */
    public Collection<String> keys(final String pattern) {
        Iterable<String> iterable = redissonClient.getKeys().getKeysByPattern(tenantEnvUtil.envJoin(pattern));
        return IterUtil.toList(iterable);
    }

    /**
     * Checks whether the key exists in Redis.
     *
     * @param key key to check
     */
    public Boolean hasKey(String key) {
        RKeys rKeys = redissonClient.getKeys();
        return rKeys.countExists(tenantEnvUtil.envJoin(key)) > 0;
    }

    /**
     * Pushes an item onto the left end of the queue.
     *
     * @param key queue key
     * @param obj item to push
     * @param <T> item type
     */
    public <T> void leftPush(String key, T obj) {
        redisTemplate.opsForList().leftPush(tenantEnvUtil.envJoin(key), obj);
    }

    /**
     * Pops an item from the right end of the queue.
     *
     * @param key   queue key
     * @param clazz item class
     * @param <T>   item type
     * @return
     */
    public <T> T rightPop(String key, Class<T> clazz) {
        return ObjectConverter.convertObj(redisTemplate.opsForList().rightPop(tenantEnvUtil.envJoin(key)), clazz);
    }

    /**
     * Atomically increments a value in a hash.
     *
     * @param key
     * @return
     */
    public Double incrementAndGet(String key, String hashKey, double value) {
        return redisTemplate.opsForHash().increment(tenantEnvUtil.envJoin(key), hashKey, value);
    }

    /**
     * Atomically increments a value in a hash.
     *
     * @param key
     * @return
     */
    public Long incrementAndGet(String key, String hashKey, Long value) {
        return redisTemplate.opsForHash().increment(tenantEnvUtil.envJoin(key), hashKey, value);
    }

}
