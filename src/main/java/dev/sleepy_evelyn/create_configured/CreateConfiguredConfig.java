package dev.sleepy_evelyn.create_configured;

import net.neoforged.neoforge.common.ModConfigSpec;

public class CreateConfiguredConfig {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue TRAIN_COLLISIONS = BUILDER
            .comment("Whether to enable collisions between Create trains.")
            .define("trainCollisions", false);

    public static final ModConfigSpec.BooleanValue CACHE_UNFILLABLE_ITEMS = BUILDER
            .comment("Introduces a cache for items that cannot be used in filling recipes.")
            .define("cacheUnfillableItems", true);

    public static final ModConfigSpec.BooleanValue REPEATING_SCHEDULE_ITEM = BUILDER
            .comment("Splits schedule items into repeating and non-repeating variants.")
            .define("repeatingScheduleItem", true);

    public static final ModConfigSpec.BooleanValue REPEATING_SCHEDULE_CRAFTABLE = BUILDER
            .comment("Determines if a repeating schedule is craftable or not.")
            .define("repeatingScheduleCraftable", true);

    static final ModConfigSpec SPEC = BUILDER.build();
}
