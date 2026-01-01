package dev.sleepy_evelyn.create_configured.mixin;

import com.simibubi.create.content.fluids.spout.FillingBySpout;
import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import dev.sleepy_evelyn.create_configured.CreateConfiguredConfig;
import dev.sleepy_evelyn.create_configured.utils.TickedCacheSet;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour.ProcessingResult.HOLD;
import static com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour.ProcessingResult.PASS;

@Restriction(
        require = {
                @Condition(value = "create", versionPredicates = ">=6.0.2")
        }
)
@Mixin(SpoutBlockEntity.class)
public abstract class SpoutBlockEntityMixin extends SmartBlockEntity {

    @Unique private final TickedCacheSet<ResourceLocation> mph$unfillableItems = new TickedCacheSet<>(1000, 20 * 60 * 5);
    @Shadow SmartFluidTankBehaviour tank;

    public SpoutBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(
            method = "onItemReceived(Lcom/simibubi/create/content/kinetics/belt/transport/TransportedItemStack;Lcom/simibubi/create/content/kinetics/belt/behaviour/TransportedItemStackHandlerBehaviour;)Lcom/simibubi/create/content/kinetics/belt/behaviour/BeltProcessingBehaviour$ProcessingResult;",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/content/fluids/spout/FillingBySpout;canItemBeFilled(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;)Z"
            ),
            cancellable = true
    )
    protected void mph$recipeItemStackCache(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler, CallbackInfoReturnable<BeltProcessingBehaviour.ProcessingResult> cir) {
        if (CreateConfiguredConfig.CACHE_UNFILLABLE_ITEMS.getAsBoolean()) mph$cacheUnfillableItems(transported, cir);
    }

    @Unique private void mph$cacheUnfillableItems(TransportedItemStack transported, CallbackInfoReturnable<BeltProcessingBehaviour.ProcessingResult> cir) {
        var transportedStack = transported.stack;
        var stackId = BuiltInRegistries.ITEM.getKey(transportedStack.getItem());

        mph$unfillableItems.tick();

        if (mph$unfillableItems.contains(stackId))
            cir.setReturnValue(PASS);
        else if (!FillingBySpout.canItemBeFilled(level, transportedStack)) {
            mph$unfillableItems.add(stackId);
            cir.setReturnValue(PASS);
        }

        if (tank.isEmpty())
            cir.setReturnValue(HOLD);
        if (FillingBySpout.getRequiredAmountForItem(level, transported.stack, tank.getPrimaryHandler().getFluid()) == -1)
            cir.setReturnValue(PASS);
        cir.setReturnValue(HOLD);
    }
}
