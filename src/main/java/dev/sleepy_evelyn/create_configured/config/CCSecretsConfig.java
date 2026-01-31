package dev.sleepy_evelyn.create_configured.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class CCSecretsConfig {
    private final ModConfigSpec.Builder builder = new ModConfigSpec.Builder().comment(
            "This config is generated and loaded only on Dedicated Servers. It holds secret values which should never be shared."
    );

    public final ModConfigSpec.ConfigValue<String> scheduleRequestsDiscordWebhookURL = builder
            .comment("""
                    .
                    Discord Webhook URL for looping Train Schedule requests. Only active if 'requestLoopingSchedules' is enabled.
                    When a player makes a request all the Schedules instructions are sent to Discord, paired with a command that can be used to approve it.
                    You can test the Webhook works by typing /train schedule test""")
            .define("scheduleRequestsDiscordWebhookURL", "https://https://discord.com/api/webhooks/[channel_id]/[token]");

    final ModConfigSpec SPEC = builder.build();
}
