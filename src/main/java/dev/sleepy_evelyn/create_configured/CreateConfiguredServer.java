package dev.sleepy_evelyn.create_configured;

import dev.sleepy_evelyn.create_configured.compat.Mods;
import dev.sleepy_evelyn.create_configured.compat.opac.OPACGroupProvider;
import dev.sleepy_evelyn.create_configured.groups.GroupsProvider;
import dev.sleepy_evelyn.create_configured.network.s2c.GroupsProviderIdPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@Mod(value = CreateConfigured.MOD_ID, dist = Dist.DEDICATED_SERVER)
@EventBusSubscriber(modid = CreateConfigured.MOD_ID, value = Dist.DEDICATED_SERVER)
public class CreateConfiguredServer {

    private static @Nullable GroupsProvider groupsProvider;

    public CreateConfiguredServer(ModContainer container) {
        if (Mods.OPENPARTIESANDCLAIMS.isLoaded()) groupsProvider = new OPACGroupProvider();
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent e) {
        if (groupsProvider != null)
            PacketDistributor.sendToPlayer((ServerPlayer) e.getEntity(),
                new GroupsProviderIdPayload(groupsProvider.id()));
    }
    public static Optional<GroupsProvider> groupsProvider() { return Optional.ofNullable(groupsProvider); }
}
