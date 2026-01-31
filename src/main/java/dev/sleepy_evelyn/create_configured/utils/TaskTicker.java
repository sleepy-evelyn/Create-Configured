package dev.sleepy_evelyn.create_configured.utils;

import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;


import java.util.*;
import java.util.function.Consumer;

@EventBusSubscriber
public final class TaskTicker {

    // Tick at which we reset to avoid long overflow
    private static final long RESET_TICK = Long.MAX_VALUE / 2;

    private static final TaskTicker ticker = new TaskTicker();

    private PriorityQueue<Task> tasks;
    private long tick;

    private TaskTicker() {
        tasks = new PriorityQueue<>(Comparator.comparingLong(task -> task.nextRun));
    }

    /**
     * Add a repeating task.
     *
     * @param interval Number of ticks between runs (> 0, <= Long.MAX_VALUE/2)
     * @param task     Consumer to execute with the server
     */
    public static void addTask(long interval, Consumer<MinecraftServer> task) {
        if (interval <= 0 || interval > Long.MAX_VALUE / 2)
            throw new IllegalArgumentException("Task interval must be > 0 & < Long.MAX_VALUE / 2");
        ticker.tasks.add(new Task(interval, ticker.tick, task));
    }

    @SubscribeEvent
    public static void increment(ServerTickEvent.Post e) {
        if (ticker.tick == RESET_TICK) reset();
        ticker.tick++;

        if (ticker.tasks.isEmpty()) return;

        var taskHead = ticker.tasks.peek();
        while (taskHead != null && taskHead.nextRun <= ticker.tick) {
            var task = ticker.tasks.remove();

            task.consumer.accept(e.getServer());
            task.nextRun += task.interval;
            ticker.tasks.add(task);
            taskHead = ticker.tasks.peek();
        }
    }

    /**
     * Reset all tasks and the tick counter to avoid overflow.
     */
    public static void reset() {
        var newTasksSet = new PriorityQueue<Task>(ticker.tasks.comparator());

        while (!ticker.tasks.isEmpty()) {
            var task = ticker.tasks.poll();
            task.nextRun = task.interval; // Rebased to 0
            newTasksSet.add(task);
        }
        ticker.tick = 0;
        ticker.tasks = newTasksSet;
    }

    private static final class Task {
        final long interval;
        long nextRun;
        final Consumer<MinecraftServer> consumer;

        private Task(long interval, long start, Consumer<MinecraftServer> consumer) {
            this.interval = interval;
            this.nextRun = start + interval;
            this.consumer = consumer;
        }
    }
}


