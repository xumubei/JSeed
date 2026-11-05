package com.jseed.satoken.starter.config;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.util.SaFoxUtil;
import com.jseed.redis.starter.util.RedisUtil;
import lombok.AllArgsConstructor;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Sa-Token persistence backed by the shared Redis utility.
 *
 * @author cyrus
 * @date: 2024-07-12
 */
@AllArgsConstructor
public class PlusSaTokenDao implements SaTokenDao {
    @Override
    public <T> T getObject(String key, Class<T> type) {
        Object value = getObject(key);
        return type.isInstance(value) ? type.cast(value) : com.jseed.core.starter.utils.ObjectConverter.convertObj(value, type);
    }


    private final RedisUtil redisUtil;
    /**
     * Maps Sa-Token's new key prefix to the legacy prefix for compatibility.
     * New key prefix
     */
    private static final String LAST_ACTIVE_PREFIX = "Authorization:login:last-active:";
    /**
     * Legacy key prefix
     */
    private static final String LAST_ACTIVITY_PREFIX = "Authorization:login:last-activity:";


    /**
     * Gets a value, or returns null if absent.
     */
    @Override
    public String get(String key) {
        key = key.replaceAll(LAST_ACTIVE_PREFIX, LAST_ACTIVITY_PREFIX);
        return redisUtil.getCacheObject(key);
    }

    /**
     * Stores a value with a TTL in seconds.
     */
    @Override
    public void set(String key, String value, long timeout) {
        key = key.replaceAll(LAST_ACTIVE_PREFIX, LAST_ACTIVITY_PREFIX);
        if (timeout == 0 || timeout <= SaTokenDao.NOT_VALUE_EXPIRE) {
            return;
        }
        // Check whether the key should never expire.
        if (timeout == SaTokenDao.NEVER_EXPIRE) {
            redisUtil.setCacheObject(key, value);
        } else {
            redisUtil.setCacheObject(key, value, Duration.ofSeconds(timeout));
        }
    }

    /**
     * Updates the key's value while keeping its expiration.
     */
    @Override
    public void update(String key, String value) {
        key = key.replaceAll(LAST_ACTIVE_PREFIX, LAST_ACTIVITY_PREFIX);
        long expire = getTimeout(key);
        // -2 means the key does not exist.
        if (expire == SaTokenDao.NOT_VALUE_EXPIRE) {
            return;
        }
        this.set(key, value, expire);
    }

    /**
     * Deletes the value.
     */
    @Override
    public void delete(String key) {
        key = key.replaceAll(LAST_ACTIVE_PREFIX, LAST_ACTIVITY_PREFIX);
        redisUtil.deleteObject(key);
    }

    /**
     * Gets the value's remaining TTL in seconds.
     */
    @Override
    public long getTimeout(String key) {
        key = key.replaceAll(LAST_ACTIVE_PREFIX, LAST_ACTIVITY_PREFIX);
        long timeout = redisUtil.getTimeToLive(key);
        return timeout < 0 ? timeout : timeout / 1000;
    }

    /**
     * Updates the value's remaining TTL in seconds.
     */
    @Override
    public void updateTimeout(String key, long timeout) {
        key = key.replaceAll(LAST_ACTIVE_PREFIX, LAST_ACTIVITY_PREFIX);
        // Check whether to remove the expiration.
        if (timeout == SaTokenDao.NEVER_EXPIRE) {
            long expire = getTimeout(key);
            if (expire == SaTokenDao.NEVER_EXPIRE) {
                // Leave the value unchanged if it already has no expiration.
            } else {
                // Rewrite the value without an expiration.
                this.set(key, this.get(key), timeout);
            }
            return;
        }
        redisUtil.expire(key, Duration.ofSeconds(timeout));
    }


    /**
     * Gets an object, or returns null if absent.
     */
    @Override
    public Object getObject(String key) {
        return redisUtil.getCacheObject(key);
    }

    /**
     * Stores an object with a TTL in seconds.
     */
    @Override
    public void setObject(String key, Object object, long timeout) {
        if (timeout == 0 || timeout <= SaTokenDao.NOT_VALUE_EXPIRE) {
            return;
        }
        // Check whether the key should never expire.
        if (timeout == SaTokenDao.NEVER_EXPIRE) {
            redisUtil.setCacheObject(key, object);
        } else {
            redisUtil.setCacheObject(key, object, Duration.ofSeconds(timeout));
        }
    }

    /**
     * Updates the object while keeping its expiration.
     */
    @Override
    public void updateObject(String key, Object object) {
        long expire = getObjectTimeout(key);
        // -2 means the key does not exist.
        if (expire == SaTokenDao.NOT_VALUE_EXPIRE) {
            return;
        }
        this.setObject(key, object, expire);
    }

    /**
     * Deletes the object.
     */
    @Override
    public void deleteObject(String key) {
        redisUtil.deleteObject(key);
    }

    /**
     * Gets the object's remaining TTL in seconds.
     */
    @Override
    public long getObjectTimeout(String key) {
        long timeout = redisUtil.getTimeToLive(key);
        return timeout < 0 ? timeout : timeout / 1000;
    }

    /**
     * Updates the object's remaining TTL in seconds.
     */
    @Override
    public void updateObjectTimeout(String key, long timeout) {
        // Check whether to remove the expiration.
        if (timeout == SaTokenDao.NEVER_EXPIRE) {
            long expire = getObjectTimeout(key);
            if (expire == SaTokenDao.NEVER_EXPIRE) {
                // Leave the value unchanged if it already has no expiration.
            } else {
                // Rewrite the value without an expiration.
                this.setObject(key, this.getObject(key), timeout);
            }
            return;
        }
        redisUtil.expire(key, Duration.ofSeconds(timeout));
    }

    @Override
    public SaSession getSession(String key) {
        return getObject(key, SaSession.class);
    }

    @Override
    public void setSession(SaSession session, long timeout) {
        setObject(session.getId(), session, timeout);
    }

    @Override
    public void updateSession(SaSession session) {
        updateObject(session.getId(), session);
    }

    @Override
    public void deleteSession(String key) {
        deleteObject(key);
    }

    @Override
    public long getSessionTimeout(String key) {
        return getObjectTimeout(key);
    }

    @Override
    public void updateSessionTimeout(String key, long timeout) {
        updateObjectTimeout(key, timeout);
    }

    @Override
    public List<String> searchData(String prefix, String keyword, int start, int size, boolean b) {
        Collection<String> keys = redisUtil.keys(prefix + "*" + keyword + "*");
        List<String> list = new ArrayList<>(keys);
        return SaFoxUtil.searchList(list, start, size, b);
    }
}
