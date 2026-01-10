package dev.sleepy_evelyn.create_configured.network;

import dev.sleepy_evelyn.create_configured.CreateConfigured;
import dev.sleepy_evelyn.create_configured.CreateConfiguredClient;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber(modid = CreateConfigured.MOD_ID)
public final class CCPackets {

    @SubscribeEvent
    public static void onRegisterPayloadHandler(RegisterPayloadHandlersEvent e) {
        var registrar = e.registrar("1");

        registrar.playToClient(
                GroupsProviderIdPayload.TYPE,
                GroupsProviderIdPayload.STREAM_CODEC,
                (payload, ctx) ->
                        CreateConfiguredClient.groupsProviderId = payload.providerId()
        );

        registrar.playToClient(
                BypassTrainDisassemblyPayload.TYPE,
                BypassTrainDisassemblyPayload.STREAM_CODEC,
                (payload, ctx) ->
                        CreateConfiguredClient.canBypassTrainDisassembly = payload.canBypassTrainDisassembly());
    }
}
