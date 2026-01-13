package dev.sleepy_evelyn.create_configured.mixin.server;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import dev.sleepy_evelyn.create_configured.TrainDisassemblyLock;
import dev.sleepy_evelyn.create_configured.mixin_interfaces.DisassemblyLockable;
import dev.sleepy_evelyn.create_configured.utils.PermissionChecks;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StationBlockEntity.class)
public class StationBlockEntityMixin implements DisassemblyLockable {

    @Unique
    private TrainDisassemblyLock cc$disassemblyLock;

    @Inject(
            method = "tryDisassembleTrain",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/content/trains/track/TrackTargetingBehaviour;getGlobalPosition()Lnet/minecraft/core/BlockPos;"
            ),
            cancellable = true
    )
    private void tryDisassembleTrain(ServerPlayer sender, CallbackInfoReturnable<Boolean> cir, @Local Train train) {
        if (!PermissionChecks.canPlayerDisassembleTrain(sender, train.owner, cc$disassemblyLock)) {
            sender.sendSystemMessage(Component.translatable("create_configured.message.train_disassembly_denied")
                    .withStyle(ChatFormatting.RED));
            cir.setReturnValue(false);
        }
    }

    @Override
    public TrainDisassemblyLock cc$getLock() {
        return cc$disassemblyLock;
    }

    @Override
    public void cc$setLock(TrainDisassemblyLock lock) {
        cc$disassemblyLock = lock;
    }
}
