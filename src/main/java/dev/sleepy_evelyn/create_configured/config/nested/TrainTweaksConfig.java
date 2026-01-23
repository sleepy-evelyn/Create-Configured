package dev.sleepy_evelyn.create_configured.config.nested;

import net.createmod.catnip.config.ConfigBase;
import org.jetbrains.annotations.NotNull;

public class TrainTweaksConfig extends ConfigBase {

    public final ConfigBool canPlayerChangeMaxSpeed = b(false, "canPlayerChangeMaxSpeed",
            "Should regular Players be allowed to change a Trains max speed. (Ignored in Singleplayer)");

    public final ConfigBool canPlayerChangeAcceleration = b(true, "canPlayerChangeMaxSpeed",
            "Should regular Players be allowed to change a Trains acceleration. (Ignored in Singleplayer)");

    public final ConfigFloat slowTopSpeedMultiplier = f(.75f, 0f, 1f, "slowTopSpeedMultiplier",
            "Top speed multiplier for slow Trains.");

    public final ConfigFloat fastTopSpeedMultiplier = f(1.5f, 1f,  "fastTopSpeedMultiplier",
            "Top speed multiplier for fast Trains.");

    public final ConfigFloat slowAccelerationMultiplier = f(.5f, 0f, 1f, "slowAccelerationMultiplier",
            "Acceleration multiplier for slow Trains.");

    public final ConfigFloat fastAccelerationMultiplier = f(1f, 1f, "fastAccelerationMultiplier",
            "Acceleration multiplier for fast Trains.");

    @Override
    public @NotNull String getName() {
        return "train_speeds";
    }
}
