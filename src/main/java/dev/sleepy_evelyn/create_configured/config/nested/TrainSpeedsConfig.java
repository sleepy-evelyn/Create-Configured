package dev.sleepy_evelyn.create_configured.config.nested;

import net.createmod.catnip.config.ConfigBase;
import org.jetbrains.annotations.NotNull;

public class TrainSpeedsConfig extends ConfigBase {

    public final ConfigBool canChangeMaxSpeed = b(true, "canChangeMaxSpeed",
            "Allow the max speed of Trains to be set to slow, default or fast in a station.");

    public final ConfigBool canPlayerChangeSpeed = b(false, "canPlayerChangeSpeed",
            "Should regular Players should be allowed to change a Trains speed. (Ignored in Singleplayer)");

    public final ConfigFloat slowTopSpeedMultiplier = f(.75f, 0f, "slowTopSpeedMultiplier",
            "Scales a Powered Trains max speed for slow Trains.");

    public final ConfigFloat fastTopSpeedMultiplier = f(1.25f, 1f,  "fastTopSpeedMultiplier",
            "Scales a Powered Trains max speed for fast Trains.");

    @Override
    public @NotNull String getName() {
        return "train_speeds";
    }
}
