package dev.sleepy_evelyn.create_configured.config;

import net.createmod.catnip.config.ConfigBase;
import org.jetbrains.annotations.NotNull;

public class CCServerConfig extends ConfigBase {

    public final ConfigBool trainCollisions = b(false, "trainCollisions",
            "Whether to enable collisions between trains. Improves server performance.");

    public final ConfigBool cacheUnfillableItems = b(true, "cacheUnfillableItems",
            "Introduces a cache for items that cannot be used in filling recipes");

    public final ConfigBool lockTrainDisassembly = b(true, "lockTrainDisassembly",
            "Allows players to lock train disassembly for individual trains");

    /*public final ConfigBool requestLoopedSchedules = b(true, "requestLoopedSchedules",
            "Require manual approval for looping schedules");*/

    @Override
    public @NotNull String getName() {
        return "server";
    }
}
