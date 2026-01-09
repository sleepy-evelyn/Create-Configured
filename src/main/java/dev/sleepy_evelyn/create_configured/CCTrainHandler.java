package dev.sleepy_evelyn.create_configured;

import dev.sleepy_evelyn.create_configured.client.CreateConfiguredClient;
import dev.sleepy_evelyn.create_configured.config.CCConfigs;
import dev.sleepy_evelyn.create_configured.network.BypassTrainDisassemblyPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.server.permission.PermissionAPI;

@EventBusSubscriber(modid = CreateConfigured.MOD_ID)
public class CCTrainHandler {

    public static final CCTrainHandler INSTANCE = new CCTrainHandler();

    private CCTrainHandler() {}

    // TODO - Add an update handler for Luckperms

    @SubscribeEvent
    public static void onRegisterPayloadHandler(RegisterPayloadHandlersEvent e) {
        var registrar = e.registrar("1");
        registrar.playToClient(
                BypassTrainDisassemblyPayload.TYPE,
                BypassTrainDisassemblyPayload.STREAM_CODEC,
                (payload, ctx) ->
                        CreateConfiguredClient.canBypassTrainDisassembly = payload.canBypass()
        );
    }

    public void sendBypassPacket(ServerPlayer player) {
        if (CCConfigs.server().lockTrainDisassembly.get())
            PacketDistributor.sendToPlayer(player,
                    new BypassTrainDisassemblyPayload(INSTANCE.canBypassDisassembly(player)));
    }

    public boolean canBypassDisassembly(ServerPlayer player) {
        return player.hasPermissions(4) || PermissionAPI.getPermission(player,
                CCPermissionNodes.BYPASS_TRAIN_DISASSEMBLY);
    }
}
