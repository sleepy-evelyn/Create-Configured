package dev.sleepy_evelyn.create_configured.utils;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

public class TickedCacheSet<T> extends ObjectOpenHashSet<T> {

    private final int maxSize;
    private final long timeoutTicks;

    private long ticksUntilClear;

    public TickedCacheSet(int maxSize, int timeoutTicks) {
        this.maxSize = maxSize;
        this.timeoutTicks = timeoutTicks;
        this.ticksUntilClear = timeoutTicks;
    }

    public void tick() {
        if (timeoutTicks > 0) {
            if (--ticksUntilClear <= 0) {
                clearAndReset();
                return;
            }
        }
        if (maxSize > 0 && size() > maxSize)
            clearAndReset();
    }

    private void clearAndReset() {
        clear();
        trim();
        ticksUntilClear = timeoutTicks;
    }
}
