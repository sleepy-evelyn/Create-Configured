package dev.sleepy_evelyn.create_configured.mixin.server;

import com.simibubi.create.content.trains.track.TrackBlock;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.sleepy_evelyn.create_configured.CreateConfigured.griefLogger;

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

        if (ctx.getPlayer() instanceof ServerPlayer serverPlayer) {
            var griefLogger = griefLogger().orElseThrow();

            if (griefLogger.isInspecting(serverPlayer))
                cir.setReturnValue(InteractionResult.FAIL);
            griefLogger.logBreakBlock(serverPlayer, level, state, ctx.getClickedPos());
        }
    }
}
