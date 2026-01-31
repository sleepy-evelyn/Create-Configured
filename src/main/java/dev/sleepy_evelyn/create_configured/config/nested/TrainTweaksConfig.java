package dev.sleepy_evelyn.create_configured.config.nested;

import net.createmod.catnip.config.ConfigBase;
import org.jetbrains.annotations.NotNull;

public class TrainTweaksConfig extends ConfigBase {

    public final ConfigBool canPlayerChangeMaxSpeed = b(true, "canPlayerChangeMaxSpeed",
            "Should Players be allowed to change a Trains max speed at a Station.",
            "(Ignored in Singleplayer)");

    public final ConfigBool canPlayerChangeAcceleration = b(true, "canPlayerChangeAcceleration",
            "Should Players be allowed to change a Trains acceleration at a Station.",
            "(Ignored in Singleplayer)");

    public final ConfigFloat fastTopSpeedMultiplier = f(1.5f, 1f,  "fastTopSpeedMultiplier",
            "Top speed multiplier for fast Trains.");

    public final ConfigFloat slowAccelerationMultiplier = f(.5f, 0f, 1f, "slowAccelerationMultiplier",
            "Acceleration multiplier for Slow Trains.");

    public final ConfigFloat fastAccelerationMultiplier = f(1.25f, 1f, "fastAccelerationMultiplier",
            "Acceleration multiplier for Fast Trains.");

    @Override
    public @NotNull String getName() {
        return "train_motion_tweaks";
    }
}
