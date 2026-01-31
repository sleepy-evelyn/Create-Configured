package dev.sleepy_evelyn.create_configured.config;

import dev.sleepy_evelyn.create_configured.config.nested.LoopingSchedulesConfig;
import dev.sleepy_evelyn.create_configured.config.nested.NumismaticsConfig;
import dev.sleepy_evelyn.create_configured.config.nested.TrainTweaksConfig;
import net.createmod.catnip.config.ConfigBase;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

public class CCServerConfig extends ConfigBase {

    public final ConfigBool trainCollisions = b(false, "trainCollisions",
            "Whether to enable collisions between trains. Improves server performance.",
            "(Ignored in Singleplayer)");

    public final ConfigBool cacheUnfillableItems = b(true, "cacheUnfillableItems",
            "Introduces a cache for items that cannot be used in filling recipes.");

    public final ConfigBool lockTrainDisassembly = b(true, "lockTrainDisassembly",
            "Allows players to lock train disassembly for individual trains.",
            "(Ignored in Singleplayer)");

    public final ConfigBool increaseMaxETATime = b(true, "increaseMaxETATime",
            "Increases max ETA time shown for Scheduled Trains on Display Boards from 10 mins to 60 mins.");

    public final TrainTweaksConfig trainTweaksConfig = nested(0, TrainTweaksConfig::new,
            "Train Motion Tweaks");

    /*public final LoopingSchedulesConfig loopingSchedulesConfig = nested(0, LoopingSchedulesConfig::new,
            "Looping Schedule Requests",
            "(Ignored in Singleplayer)");

    public final NumismaticsConfig numismaticsConfig = nested(0, NumismaticsConfig::new,
            "Numismatics");*/


    @Override
    public @NotNull String getName() {
        return "server";
    }
}
