package dev.sleepy_evelyn.create_configured.mixin.server;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import dev.sleepy_evelyn.create_configured.CreateConfigured;
import dev.sleepy_evelyn.create_configured.CreateConfiguredServer;
import dev.sleepy_evelyn.create_configured.utils.TrainHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = StationBlockEntity.class, remap = false)
public abstract class StationBlockEntityMixin {

    @Inject(
            method = "tryDisassembleTrain",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/content/trains/track/TrackTargetingBehaviour;getGlobalPosition()Lnet/minecraft/core/BlockPos;"
            ),
            cancellable = true
    )
    private void tryDisassembleTrain(@Nullable ServerPlayer sender, CallbackInfoReturnable<Boolean> cir, @Local Train train) {
        if (!TrainHelper.canDisassemble(sender, train)) {
            if (sender != null)
                sender.sendSystemMessage(Component.translatable("create_configured.message.train.disassembly_denied")
                        .withStyle(ChatFormatting.RED));
            cir.setReturnValue(false);
        }
    }
}
