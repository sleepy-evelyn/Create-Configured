package dev.sleepy_evelyn.create_configured.network;

import com.simibubi.create.content.trains.station.StationBlockEntity;
import dev.sleepy_evelyn.create_configured.CreateConfigured;
import dev.sleepy_evelyn.create_configured.config.CCConfigs;
import dev.sleepy_evelyn.create_configured.mixin_interfaces.server.DisassemblyLockable;
import dev.sleepy_evelyn.create_configured.network.c2s.ChangeDisassemblyLockPayload;
import dev.sleepy_evelyn.create_configured.network.c2s.NotifyTrainAtStation;
import dev.sleepy_evelyn.create_configured.network.s2c.StationScreenSyncPayload;
import dev.sleepy_evelyn.create_configured.utils.TrainHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Objects;

import static dev.sleepy_evelyn.create_configured.CreateConfigured.isDedicatedServer;

@EventBusSubscriber(modid = CreateConfigured.MOD_ID)
public final class CCServerboundPackets {

    @SubscribeEvent
    public static void onRegisterPayloadHandler(RegisterPayloadHandlersEvent e) {
        var registrar = e.registrar("1");

        registrar.playToServer(ChangeDisassemblyLockPayload.TYPE, ChangeDisassemblyLockPayload.STREAM_CODEC,
                CCServerboundPackets::changeDisassemblyLockHandler);

        registrar.playToServer(NotifyTrainAtStation.TYPE, NotifyTrainAtStation.STREAM_CODEC,
                CCServerboundPackets::notifyTrainAtStationHandler);
    }

    private static void notifyTrainAtStationHandler(NotifyTrainAtStation payload, IPayloadContext ctx) {
        var level = ctx.player().level();
        if (!isDedicatedServer(level)) return;

        if (level.getBlockEntity(payload.stationPos()) instanceof StationBlockEntity sbe)
            TrainHelper.getOwnedTrain(sbe).ifPresent(train -> {
                var lock = ((DisassemblyLockable) train).cc$getLock();
                var serverPlayer = (ServerPlayer) ctx.player();

                PacketDistributor.sendToPlayer(serverPlayer,
                        new StationScreenSyncPayload(TrainHelper.canPlayerDisassemble(serverPlayer, train), lock));
            });
    }

    private static void changeDisassemblyLockHandler(ChangeDisassemblyLockPayload payload, IPayloadContext ctx) {
        var level = ctx.player().level();
        if (!isDedicatedServer(level)) return;

        var stationPos = payload.stationPos();
        var disassemblyLock = payload.lock();
        var player = ctx.player();

        if (level.getBlockEntity(stationPos) instanceof StationBlockEntity sbe) {
            var trainOptional = TrainHelper.getOwnedTrain(sbe);

            if (trainOptional.isPresent()) {
                var train = trainOptional.get();

                if (Objects.equals(train.owner, player.getUUID()))
                    ((DisassemblyLockable) train).cc$setLock(disassemblyLock);
                else
                    player.sendSystemMessage(Component.translatable("create_configured.message.train.lock_change_denied")
                            .withStyle(ChatFormatting.RED));
            }
        }

    }
}
