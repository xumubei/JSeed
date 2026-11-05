

package com.jseed.redis.starter.lock;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.jseed.core.starter.exception.ServerException;
import com.jseed.redis.starter.util.TenantEnvUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.RedissonMultiLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Distributed lock implementation
 *
 * @author cyrus
 * @date: 2024-07-10
 */
@Slf4j
@AllArgsConstructor
public class RedisDistributedLock implements ILock {

    private final RedissonClient redissonClient;
    private final TenantEnvUtil tenantEnvUtil;

    private static final String PREFIX = "DISTRIBUTED_LOCK_";

    @Override
    public <T> T tryLock(String lockKey, long waitTime, long leaseTime, Supplier<T> lockCallBack) {
        if (StrUtil.isBlank(lockKey)) {
            throw new RuntimeException("lockKey不能为空");
        }

        this.checkParam(waitTime, leaseTime, lockCallBack);
        lockKey = PREFIX + tenantEnvUtil.envJoin(lockKey);
        RLock lock = this.redissonClient.getLock(lockKey);
        return this.doLock(lock, lockKey, waitTime, leaseTime, lockCallBack);
    }

    @Override
    public <T> T tryLock(String lockKey, Supplier<T> lockCallBack) {
        return this.tryLock(lockKey, 1000, 30000, lockCallBack);
    }

    private <T> T doLock(RLock lock, Object lockKey, long waitTime, long leaseTime, Supplier<T> lockCallBack) {
        boolean locked = false;
        try {
            locked = lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS);
        } catch (Throwable e) {
            throw new RuntimeException(String.format("获取分布式锁异常lockKey:%s", lockKey), e);
        }

        if (!locked) {
            throw new ServerException(String.format("等待%s毫秒后获取分布式锁失败lockKey:%s", waitTime, lockKey));
        }
        T object;
        try {
            object = lockCallBack.get();
        } catch (Throwable e) {
            throw e;
        } finally {
            if (lock instanceof RedissonMultiLock) {
                lock.unlock();
            } else if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return object;
    }

    @Override
    public <T> T tryMultiLock(List<String> lockKeys, Supplier<T> lockCallBack) {
        return this.tryMultiLock(lockKeys, 10000L, -1L, lockCallBack);
    }

    @Override
    public <T> T tryMultiLock(List<String> lockKeys, long waitTime, long leaseTime, Supplier<T> lockCallBack) {
        if (CollectionUtil.isEmpty(lockKeys)) {
            throw new RuntimeException("分布式锁MULTI模式下lockKeys不能为空");
        }

        this.checkParam(waitTime, leaseTime, lockCallBack);
        RLock[] rLocks = new RLock[lockKeys.size()];
        lockKeys.sort(String::compareTo);
        int i = 0;

        for (int length = lockKeys.size(); i < length; ++i) {
            String keys = PREFIX + tenantEnvUtil.envJoin(lockKeys.get(i));
            keys = PREFIX + keys;
            lockKeys.set(i, keys);
            RLock lock = this.redissonClient.getLock(keys);
            rLocks[i] = lock;
        }

        RedissonMultiLock redissonMultiLock = new RedissonMultiLock(rLocks);
        return this.doLock(redissonMultiLock, lockKeys, waitTime, leaseTime, lockCallBack);
    }

    private <T> void checkParam(long waitTime, long leaseTime, Supplier<T> lockCallBack) {
        if (lockCallBack == null) {
            throw new RuntimeException("lockCallBack不能为空");
        }

        if (waitTime <= 0L) {
            throw new RuntimeException("waitTime不能为负数");
        }

        if (leaseTime == 0L) {
            throw new RuntimeException("leaseTime不能为0");
        }

    }
}
