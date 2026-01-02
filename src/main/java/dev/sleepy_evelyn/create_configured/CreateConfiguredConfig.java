package dev.sleepy_evelyn.create_configured;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class CreateConfiguredConfig {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue TRAIN_COLLISIONS = BUILDER
            .comment("Whether to enable collisions between trains.")
            .define("trainCollisions", false);

    public static final ModConfigSpec.BooleanValue CACHE_UNFILLABLE_ITEMS = BUILDER
            .comment("Introduces a cache for items that cannot be used in filling recipes.")
            .define("cacheUnfillableItems", true);

    public static final ModConfigSpec.BooleanValue REQUEST_LOOPED_SCHEDULES = BUILDER
            .comment("Require manual approval for looping schedules.")
            .define("requestLoopedSchedules", true);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> SCHEDULE_APPROVERS = BUILDER
            .comment("Which players are able to approve looping schedules. Defaults to all Operators if left blank.")
            .defineListAllowEmpty("trainScheduleApprovers", List.of(), () -> "", CreateConfiguredConfig::validatePlayerName);

    private static boolean validatePlayerName(final Object playerNameObj) {
        return (playerNameObj instanceof String name) &&
                name.length() >= 3 && name.length() <= 16 && name.matches("[A-Za-z0-9_]+");
    }

    static final ModConfigSpec SPEC = BUILDER.build();
}
