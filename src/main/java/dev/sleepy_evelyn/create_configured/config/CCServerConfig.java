package dev.sleepy_evelyn.create_configured.config;

import net.createmod.catnip.config.ConfigBase;
import org.jetbrains.annotations.NotNull;

public class CCServerConfig extends ConfigBase {

    public final ConfigBool trainCollisions = b(false, "trainCollisions",
            "Whether to enable collisions between trains. Improved server performance.");

    public final ConfigBool cacheUnfillableItems = b(true, "cacheUnfillableItems",
            "Introduces a cache for items that cannot be used in filling recipes");

    public final ConfigBool lockTrainDisassembly = b(true, "lockTrainDisassembly",
            "Only a trains owner, an operator, or a player / group with appropriate permission may disassemble a train");

    /*public final ConfigBool requestLoopedSchedules = b(true, "requestLoopedSchedules",
            "Require manual approval for looping schedules");*/

    @Override
    public @NotNull String getName() {
        return "server";
    }
}
