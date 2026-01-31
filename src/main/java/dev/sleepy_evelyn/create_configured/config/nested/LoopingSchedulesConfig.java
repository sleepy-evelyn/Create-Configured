package dev.sleepy_evelyn.create_configured.config.nested;

import dev.sleepy_evelyn.create_configured.trains.LoopingScheduleAction;
import net.createmod.catnip.config.ConfigBase;
import org.jetbrains.annotations.NotNull;

public class LoopingSchedulesConfig extends ConfigBase {

    public final ConfigBool requestLoopingSchedules = b(false, "requestLoopingSchedules",
            "Whether looping train schedules need to be requested and approved.",
                    "Set a Discord Webhook URL in the 'create-configured-secrets.toml' config so requests can be sent.");

    public final ConfigInt gracePeriodDays = i(10, 1,"gracePeriod",
            "[In Days]", "Grace period before a looping schedule request is either automatically approved, denied or no " +
                    "further action is taken.");

    public final ConfigEnum<LoopingScheduleAction> approvalStrategy = e(LoopingScheduleAction.DENY,
            "approvalStrategy", "What action is taken after the grace period has ended.");

    @Override
    public @NotNull String getName() { return "looping_schedules"; }
}
