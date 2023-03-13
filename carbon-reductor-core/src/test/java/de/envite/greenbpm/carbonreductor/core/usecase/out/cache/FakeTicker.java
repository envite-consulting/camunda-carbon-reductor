package de.envite.greenbpm.carbonreductor.core.usecase.out.cache;

import com.github.benmanes.caffeine.cache.Ticker;
import lombok.NoArgsConstructor;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@NoArgsConstructor
class FakeTicker implements Ticker {

    private final AtomicLong nanos = new AtomicLong();
    private volatile long autoIncrementStepNanos;

    public FakeTicker advance(long time, TimeUnit timeUnit) {
        return advance(timeUnit.toNanos(time));
    }

    public FakeTicker advance(long nanoseconds) {
        this.nanos.addAndGet(nanoseconds);
        return this;
    }

    public FakeTicker advance(java.time.Duration duration) {
        return advance(duration.toNanos());
    }

    public FakeTicker setAutoIncrementStep(long autoIncrementStep, TimeUnit timeUnit) {
        if (autoIncrementStep < 0L) {
            throw new IllegalArgumentException("May not auto-increment by a negative amount");
        }
        this.autoIncrementStepNanos = timeUnit.toNanos(autoIncrementStep);
        return this;
    }

    public FakeTicker setAutoIncrementStep(java.time.Duration autoIncrementStep) {
        return setAutoIncrementStep(autoIncrementStep.toNanos(), TimeUnit.NANOSECONDS);
    }

    @Override
    public long read() {
        return nanos.getAndAdd(autoIncrementStepNanos);
    }
}
