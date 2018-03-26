package io.inbot.utils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * Supplier that caches and re-initializes it's value. Useful to encapsulate expensive initialization logic to ensure
 * it only gets executed if it did not run recently.
 * @param <V> the type of the object that is supplied.
 */
public class ReInitializingReference<V> implements Supplier<V> {
    private volatile V instance;
    private volatile long lastInitialized=0;
    private final Supplier<V> initializer;
    private final ReentrantLock lock = new ReentrantLock();
    private final long expirationInMillis;

    public ReInitializingReference(Supplier<V> supplier, long expiration, TimeUnit unit) {
        this.initializer = supplier;
        this.expirationInMillis = unit.toMillis(expiration);
    }

    public V get() {
        if(needsReinitialization()) {
            lock.lock();
            try {
                if(needsReinitialization()) {
                    instance = initializer.get();
                    lastInitialized=System.currentTimeMillis();
                }
            } finally {
                lock.unlock();
            }
        }
        return instance;
    }

    /**
     * Forces reinitialization on next get()
     */
    public void reset() {
        lock.lock();
        try {
            // force reinitialization on next get
            lastInitialized=0;
        } finally {
            lock.unlock();
        }
    }

    private boolean needsReinitialization() {
        return instance==null || System.currentTimeMillis() - lastInitialized > expirationInMillis;
    }
}
