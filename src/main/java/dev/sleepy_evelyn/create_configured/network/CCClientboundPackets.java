package dev.sleepy_evelyn.create_configured.network;

import dev.sleepy_evelyn.create_configured.CreateConfigured;
import dev.sleepy_evelyn.create_configured.CreateConfiguredClient;
import dev.sleepy_evelyn.create_configured.network.s2c.GroupsProviderIdPayload;
import dev.sleepy_evelyn.create_configured.network.s2c.StationScreenSyncPayload;
import dev.sleepy_evelyn.create_configured.utils.ScreenUtils;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber(modid = CreateConfigured.MOD_ID)
public final class CCClientboundPackets {

    @SubscribeEvent
    public static void onRegisterPayloadHandler(RegisterPayloadHandlersEvent e) {
        var registrar = e.registrar("1");

        registrar.playToClient(GroupsProviderIdPayload.TYPE, GroupsProviderIdPayload.STREAM_CODEC,
                (payload, ctx) ->
                        CreateConfiguredClient.groupsProviderId = payload.providerId());

        registrar.playToClient(StationScreenSyncPayload.TYPE, StationScreenSyncPayload.STREAM_CODEC,
                (payload, ctx) -> ScreenUtils.syncStationScreen(payload));
    }
}
