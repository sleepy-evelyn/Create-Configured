package dev.sleepy_evelyn.create_configured.network;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.schedule.Schedule;
import dev.sleepy_evelyn.create_configured.CreateConfigured;
import dev.sleepy_evelyn.create_configured.mixin_interfaces.server.TrainTweaks;
import dev.sleepy_evelyn.create_configured.network.c2s.ChangeDisassemblyLockPayload;
import dev.sleepy_evelyn.create_configured.network.c2s.ChangeMotionProfilePayload;
import dev.sleepy_evelyn.create_configured.network.c2s.LoopingScheduleActionPayload;
import dev.sleepy_evelyn.create_configured.network.c2s.NotifyTrainAtStation;
import dev.sleepy_evelyn.create_configured.network.s2c.StationScreenSyncPayload;
import dev.sleepy_evelyn.create_configured.permissions.TrainPermissionChecks;
import dev.sleepy_evelyn.create_configured.permissions.TrainTweakPermissions;
import dev.sleepy_evelyn.create_configured.trains.DiscordSchedule;
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
import static dev.sleepy_evelyn.create_configured.utils.TrainUtils.getOwnedTrainFromStationPos;

@EventBusSubscriber
public final class CCServerboundPackets {

    @SubscribeEvent
    public static void onRegisterPayloadHandler(RegisterPayloadHandlersEvent e) {
        var registrar = e.registrar("1");

        registrar.playToServer(LoopingScheduleActionPayload.TYPE, LoopingScheduleActionPayload.STREAM_CODEC,
                CCServerboundPackets::sendLoopingScheduleAction);

        registrar.playToServer(NotifyTrainAtStation.TYPE, NotifyTrainAtStation.STREAM_CODEC,
                CCServerboundPackets::notifyTrainAtStationHandler);

        registrar.playToServer(ChangeMotionProfilePayload.TYPE, ChangeMotionProfilePayload.STREAM_CODEC,
                CCServerboundPackets::changeMotionProfileHandler);

        registrar.playToServer(ChangeDisassemblyLockPayload.TYPE, ChangeDisassemblyLockPayload.STREAM_CODEC,
                CCServerboundPackets::changeDisassemblyLockHandler);
    }

    private static void sendLoopingScheduleAction(LoopingScheduleActionPayload payload, IPayloadContext ctx) {
        var discordSchedule = new DiscordSchedule((ServerPlayer) ctx.player(), payload.schedule().entries);
        discordSchedule.send();
    }

    private static void notifyTrainAtStationHandler(NotifyTrainAtStation payload, IPayloadContext ctx) {
        var level = ctx.player().level();

        getOwnedTrainFromStationPos(level, payload.stationPos()).ifPresent(train -> {
            var trainTweaks = (TrainTweaks) train;
            var serverPlayer = (ServerPlayer) ctx.player();
            var permissions = TrainTweakPermissions.resolve(serverPlayer, train);

            PacketDistributor.sendToPlayer(serverPlayer, new StationScreenSyncPayload(permissions,
                    trainTweaks.cc$getLock(), trainTweaks.cc$getTopSpeed(), trainTweaks.cc$getAcceleration()));
        });
    }

    private static void changeMotionProfileHandler(ChangeMotionProfilePayload payload, IPayloadContext ctx) {
        var serverPlayer = (ServerPlayer) ctx.player();
        var level = serverPlayer.level();

        getOwnedTrainFromStationPos(level, payload.stationPos()).ifPresent(train -> {
            var motionProfile = payload.motionProfile();

            if(TrainPermissionChecks.canChangeMotionProfile(serverPlayer, motionProfile.getType()))
                ((TrainTweaks) train).cc$setMotionProfile(payload.motionProfile());
        });
    }

    private static void changeDisassemblyLockHandler(ChangeDisassemblyLockPayload payload, IPayloadContext ctx) {
        var level = ctx.player().level();
        if (!isDedicatedServer(level)) return;

        var disassemblyLock = payload.lock();
        var player = ctx.player();

        getOwnedTrainFromStationPos(level, payload.stationPos()).ifPresent(train -> {
            if (Objects.equals(train.owner, player.getUUID()))
                ((TrainTweaks) train).cc$setLock(disassemblyLock);
            else
                player.sendSystemMessage(Component.translatable("create_configured.message.train.lock_change_denied")
                        .withStyle(ChatFormatting.RED));
        });
    }
}
