package dev.sleepy_evelyn.create_configured.mixin;

import com.daqem.grieflogger.event.block.LogBlockEvent;
import com.daqem.grieflogger.model.action.BlockAction;
import com.daqem.grieflogger.player.GriefLoggerServerPlayer;
import com.simibubi.create.content.trains.track.TrackBlock;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Restriction(
        require = {
                @Condition(value = "grieflogger", versionPredicates = ">=1.2.6")
        }
)
@Mixin(TrackBlock.class)
public class TrackBlockMixin {

    @Inject(method = "onSneakWrenched", at = @At("HEAD"), cancellable = true)
    private void logSneakWrench(BlockState state, UseOnContext ctx, CallbackInfoReturnable<InteractionResult> cir) {
        var level = ctx.getLevel();

        if (!level.isClientSide) {
            var server = level.getServer();

            if (server != null && server.isDedicatedServer() && ctx.getPlayer() instanceof GriefLoggerServerPlayer glsp) {
                if (glsp.grieflogger$isInspecting())
                    cir.setReturnValue(InteractionResult.FAIL);

                LogBlockEvent.logBlock(glsp, level, state, ctx.getClickedPos(), BlockAction.BREAK_BLOCK);
            }
        }
    }
}
