package dev.sleepy_evelyn.create_configured.mixin.server;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.trains.station.StationBlock;
import dev.sleepy_evelyn.create_configured.config.CCConfigs;
import dev.sleepy_evelyn.create_configured.network.BypassTrainDisassemblyPayload;
import dev.sleepy_evelyn.create_configured.utils.PermissionChecks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StationBlock.class)
public class StationBlockMixin {

    @Inject(
            method = "useItemOn(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/ItemInteractionResult;",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lcom/simibubi/create/content/trains/station/StationBlock;onBlockEntityUse(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Ljava/util/function/Function;)Lnet/minecraft/world/InteractionResult;"
            )
    )
    private void beforeOpenStationScreen(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<ItemInteractionResult> cir, @Local InteractionResult interactionResult) {
        if (interactionResult == InteractionResult.PASS) {
            var serverPlayer = (ServerPlayer) player;
            boolean canBypassTrainDisassembly = !CCConfigs.server().lockTrainDisassembly.get() &&
                    PermissionChecks.canBypassTrainDisassembly(serverPlayer);

            PacketDistributor.sendToPlayer(serverPlayer,
                    new BypassTrainDisassemblyPayload(canBypassTrainDisassembly));
        }
    }
}
