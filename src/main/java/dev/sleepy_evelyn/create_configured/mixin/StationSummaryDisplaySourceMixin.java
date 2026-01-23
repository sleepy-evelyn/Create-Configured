package dev.sleepy_evelyn.create_configured.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.content.redstone.displayLink.source.StationSummaryDisplaySource;
import dev.sleepy_evelyn.create_configured.config.CCConfigs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;

import static com.simibubi.create.content.trains.display.FlapDisplaySection.MONOSPACE;

/**
 * Increases max ETA time shown on Display Boards from 12000 ticks (10 min) to 72000 ticks (60 min)
 *
 * @author Yokuyin
 */
@Mixin(value = StationSummaryDisplaySource.class, remap = false)
public abstract class StationSummaryDisplaySourceMixin extends DisplaySource {

    @ModifyExpressionValue(
            method = "lambda$provideFlapDisplayText$0",
            at = @At(
                    value = "CONSTANT",
                    args = "intValue=11700"
            )
    )
    private static int increaseEtaDisplayLimitTo60m(int original) {
        // 10 minutes > 60 minutes
        return cc$increaseMaxEtaTime() ? 72000 - 15 * 20 : original;
    }

    @ModifyArg(
            method = "loadFlapDisplayLayout",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/content/trains/display/FlapDisplaySection;<init>(FLjava/lang/String;ZZ)V",
                    ordinal = 0
            ),
            index = 0
    )
    private float increaseMinuteSectionOfDisplayBoard(float width) {
        return cc$increaseMaxEtaTime() ? MONOSPACE * 2 : width;
    }

    @ModifyVariable(method = "loadFlapDisplayLayout", at = @At("STORE"), name = "totalSize")
    private float decreaseAvailableSpaceForOtherSections(float totalSize) {
        // Subtract the amount added above
        return cc$increaseMaxEtaTime() ? totalSize - MONOSPACE : totalSize;
    }

    @Unique
    private static boolean cc$increaseMaxEtaTime() {
        return CCConfigs.server().increaseMaxETATime.get();
    }
}
