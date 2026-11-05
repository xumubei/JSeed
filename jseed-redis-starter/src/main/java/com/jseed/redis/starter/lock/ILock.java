package com.jseed.redis.starter.lock;

import java.util.List;
import java.util.function.Supplier;

/**
 * Distributed lock API
 *
 * @author cyrus
 * @date: 2024-07-10
 */
public interface ILock {
    /**
     * Runs an operation under a lock with the specified wait and lease times.
     *
     * @param lockKey      lock key
     * @param waitTime     time to wait for the lock
     * @param leaseTime    lock lease duration
     * @param lockCallBack operation to execute
     * @param <T>
     * @return
     */
    <T> T tryLock(String lockKey, long waitTime, long leaseTime, Supplier<T> lockCallBack);

    /**
     * Acquires locks for multiple keys.
     *
     * @param lockKeys     lock keys
     * @param waitTime     time to wait for the lock
     * @param leaseTime    lock lease duration
     * @param lockCallBack operation to execute
     * @param <T>
     * @return
     */
    <T> T tryMultiLock(List<String> lockKeys, long waitTime, long leaseTime, Supplier<T> lockCallBack);

    /**
     * Runs an operation under a lock using the default wait and lease times.
     * The default wait is 10 seconds and the default lease is 30 seconds.
     *
     * @param lockKey      lock key
     * @param lockCallBack operation to execute
     * @param <T>
     * @return
     */
    <T> T tryLock(String lockKey, Supplier<T> lockCallBack);

    /**
     * Acquires locks for multiple keys.
     * The default wait is 10 seconds and the default lease is 30 seconds.
     *
     * @param lockKeys     keys to lock
     * @param lockCallBack operation to execute
     * @param <T>
     * @return
     * @see ILock#tryLock(String, Supplier)
     */
    <T> T tryMultiLock(List<String> lockKeys, Supplier<T> lockCallBack);

    /**
     * Wraps an action as a supplier that returns null.
     *
     * @param runnable
     * @param <T>
     * @return
     */
    default <T> Supplier<T> emptyReturn(Runnable runnable) {
        return () -> {
            runnable.run();
            return null;
        };
    }

}
