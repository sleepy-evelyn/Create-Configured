package dev.sleepy_evelyn.create_configured;

import dev.sleepy_evelyn.create_configured.commands.CCCommands;
import dev.sleepy_evelyn.create_configured.compat.Mods;
import dev.sleepy_evelyn.create_configured.compat.opac.OPACGroupProvider;
import dev.sleepy_evelyn.create_configured.config.CCConfigs;
import dev.sleepy_evelyn.create_configured.groups.GroupsProvider;
import dev.sleepy_evelyn.create_configured.network.s2c.GroupsProviderIdPayload;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.EnchantedBookItem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@Mod(value = CreateConfigured.MOD_ID, dist = Dist.DEDICATED_SERVER)
@EventBusSubscriber(value = Dist.DEDICATED_SERVER)
public class CreateConfiguredServer {

    private static @Nullable GroupsProvider groupsProvider;

    public CreateConfiguredServer(ModContainer container) {
        if (Mods.OPENPARTIESANDCLAIMS.isLoaded()) groupsProvider = new OPACGroupProvider();
        CCConfigs.registerDedicated(container);
    }

    private static void onConfigReloaded(MinecraftServer server) {
        var loopingSchedulesConfig = CCConfigs.server().loopingSchedulesConfig;
        var requestLoopingSchedules = loopingSchedulesConfig.requestLoopingSchedules.get();

        if (!requestLoopingSchedules) return;

        // Send a notification if the Discord Webhook hasn't been changed from the default config value
        CCConfigs.secrets().ifPresent(secrets -> {
            String defaultWebhookUrl = secrets.scheduleRequestsDiscordWebhookURL.getDefault();
            String webhookUrl = secrets.scheduleRequestsDiscordWebhookURL.get();

            if (!webhookUrl.equals(defaultWebhookUrl)) return;
            for (var player : server.getPlayerList().getPlayers())
                player.sendSystemMessage(
                        Component.translatable("create_configured.message.prefix")
                                .withStyle(ChatFormatting.BLUE).append(
                                Component.translatable("create_configured.message.config.secrets." +
                                        "no_schedule_request_discord_webhook").withStyle(ChatFormatting.GRAY)));
        });
    }

    @SubscribeEvent
    public static void onCommandRegistration(RegisterCommandsEvent e) {
        CCCommands.register(e.getDispatcher(), e.getBuildContext(), e.getCommandSelection());
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent e) {
        if (groupsProvider != null)
            PacketDistributor.sendToPlayer((ServerPlayer) e.getEntity(),
                new GroupsProviderIdPayload(groupsProvider.id()));
    }

    public static Optional<GroupsProvider> groupsProvider() { return Optional.ofNullable(groupsProvider); }
}
