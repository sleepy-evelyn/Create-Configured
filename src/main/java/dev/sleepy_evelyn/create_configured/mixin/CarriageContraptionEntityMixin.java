package dev.sleepy_evelyn.create_configured.mixin;

import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import dev.sleepy_evelyn.create_configured.mixin_interfaces.server.TrainTweaks;
import dev.sleepy_evelyn.create_configured.network.s2c.TrainHUDTopSpeedPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CarriageContraptionEntity.class)
public class CarriageContraptionEntityMixin {

    @Shadow private Carriage carriage;

    @Inject(method = "startControlling", at = @At("TAIL"))
    public void startControlling(BlockPos controlsLocalPos, Player player, CallbackInfoReturnable<Boolean> cir) {
        if (player instanceof ServerPlayer serverPlayer)
            PacketDistributor.sendToPlayer(serverPlayer,
                    new TrainHUDTopSpeedPacket(carriage.train.id, ((TrainTweaks) carriage.train).cc$getTopSpeed()));
    }
}
