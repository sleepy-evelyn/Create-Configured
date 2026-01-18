package dev.sleepy_evelyn.create_configured.mixin.server;

import com.simibubi.create.content.equipment.wrench.WrenchItem;
import com.simibubi.create.content.kinetics.deployer.DeployerFakePlayer;
import com.simibubi.create.content.trains.station.StationBlock;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import dev.sleepy_evelyn.create_configured.utils.TrainPermissionChecks;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Restriction(require = @Condition(value = "railways"))
@Mixin(value = WrenchItem.class, remap = false, priority = 0)
public class MixinWrenchItem {

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true, remap = false)
    private void deployerWrenchStationInteraction(UseOnContext ctx, CallbackInfoReturnable<InteractionResult> cir) {
        if (ctx.getPlayer() instanceof DeployerFakePlayer deployerFakePlayer) {
            var level = ctx.getLevel();
            var clickedPos = ctx.getClickedPos();
            var state = level.getBlockState(clickedPos);

            if (level.getBlockEntity(clickedPos) instanceof StationBlockEntity stationBE) {
                if (state.getValue(StationBlock.ASSEMBLING)) return;

                TrainPermissionChecks.getOwnedTrain(stationBE).ifPresent(train -> {
                    UUID deployerOwner = deployerFakePlayer.getUUID();

                    if (!TrainPermissionChecks.canDisassemble(level.getServer(), deployerOwner, train))
                        cir.setReturnValue(InteractionResult.FAIL);
                });
            }
        }
    }
}
