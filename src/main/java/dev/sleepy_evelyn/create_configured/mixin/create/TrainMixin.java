package dev.sleepy_evelyn.create_configured.mixin.create;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.TrackGraph;
import dev.sleepy_evelyn.create_configured.mixin_interfaces.server.TrainTweaks;
import dev.sleepy_evelyn.create_configured.trains.TrainDisassemblyLock;
import dev.sleepy_evelyn.create_configured.trains.TrainMotionProfile;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.fml.loading.FMLEnvironment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mixin(Train.class)
public class TrainMixin implements TrainTweaks {

    @Shadow public int fuelTicks;
    @Unique private TrainDisassemblyLock cc$disassemblyLock = TrainDisassemblyLock.NOT_LOCKED;
    @Unique private TrainMotionProfile cc$topSpeed = TrainMotionProfile.DEFAULT_TOP_SPEED;
    @Unique private TrainMotionProfile cc$acceleration = TrainMotionProfile.DEFAULT_ACCELERATION;

    @Unique int cc$fuelTicksSubCounter, cc$fuelMultiplier;

    @ModifyReturnValue(method = "maxSpeed", at = @At("RETURN"))
    private float maxSpeed(float original) {
        return original * cc$topSpeed.getMultiplier(fuelTicks > 0);
    }

    @ModifyReturnValue(method = "maxTurnSpeed", at = @At("RETURN"))
    private float maxTurnSpeed(float original) {
        return original * cc$topSpeed.getMultiplier(fuelTicks > 0);
    }

    @ModifyReturnValue(method = "acceleration", at = @At("RETURN"))
    private float acceleration(float original) {
        return original * cc$acceleration.getMultiplier(fuelTicks > 0);
    }

    @Inject(method = "burnFuel", at = @At("HEAD"), cancellable = true)
    private void modifyBurnTime(CallbackInfo ci) {
        cc$fuelMultiplier = 1;
        if (fuelTicks > 0) {
            if (cc$acceleration != TrainMotionProfile.DEFAULT_ACCELERATION)
                cc$fuelMultiplier += cc$acceleration == TrainMotionProfile.FAST_ACCELERATION ? 1 : -1;
            if (cc$topSpeed == TrainMotionProfile.FAST_TOP_SPEED)
                cc$fuelMultiplier++;

            if (cc$fuelMultiplier > 1) {
                fuelTicks = Math.max(0, fuelTicks - cc$fuelMultiplier);
                ci.cancel();
            } else if (cc$fuelMultiplier <= 0) {
                if (cc$fuelTicksSubCounter >= 1) {
                    fuelTicks--;
                    cc$fuelTicksSubCounter = 0;
                } else
                    cc$fuelTicksSubCounter++;
                ci.cancel();
            }
        }
    }

    @Inject(
            method = "burnFuel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/neoforged/neoforge/items/IItemHandlerModifiable;extractItem(IIZ)Lnet/minecraft/world/item/ItemStack;",
                    ordinal = 1
            )
    )
    private void modifyBurnTime(CallbackInfo ci, @Local(name = "burnTime") LocalIntRef burnTime) {

    }

    @Inject(method = "read", at = @At("TAIL"))
    private static void read(CompoundTag tag, HolderLookup.Provider registries, Map<UUID, TrackGraph> trackNetworks, DimensionPalette dimensions, CallbackInfoReturnable<Train> cir, @Local(name = "train") Train train) {
        TrainTweaks disassemblyLockable = (TrainTweaks) train;

        if (tag.contains("DisassemblyLock") && FMLEnvironment.dist.isDedicatedServer())
            disassemblyLockable.cc$setLock(TrainDisassemblyLock.BY_ID.apply(tag.getInt("DisassemblyLock")));

        if (tag.contains("MotionProfile")) {
            int[] motionProfile = tag.getIntArray("MotionProfile");

            if (motionProfile.length == 2) {
                var topSpeedProfile = TrainMotionProfile.BY_ID.apply(motionProfile[0]);
                var accelerationProfile = TrainMotionProfile.BY_ID.apply(motionProfile[1]);

                if (topSpeedProfile == TrainMotionProfile.SLOW_TOP_SPEED)
                    topSpeedProfile = TrainMotionProfile.DEFAULT_TOP_SPEED;

                disassemblyLockable.cc$setMotionProfile(topSpeedProfile);
                disassemblyLockable.cc$setMotionProfile(accelerationProfile);
            }
        }
    }

    @Inject(method = "write", at = @At("RETURN"))
    private void write(DimensionPalette dimensions, HolderLookup.Provider registries, CallbackInfoReturnable<CompoundTag> cir) {
        var tag = cir.getReturnValue();

        if (FMLEnvironment.dist.isDedicatedServer()) tag.putInt("DisassemblyLock", cc$disassemblyLock.getId());
        tag.putIntArray("MotionProfile", List.of(cc$topSpeed.getId(), cc$acceleration.getId()));
    }

    @Override public void cc$setLock(TrainDisassemblyLock lock) { cc$disassemblyLock = lock; }
    @Override public void cc$setMotionProfile(TrainMotionProfile motionProfile) {
        if (motionProfile.getType() == TrainMotionProfile.Type.TOP_SPEED)
            cc$topSpeed = motionProfile;
        else if (motionProfile.getType() == TrainMotionProfile.Type.ACCELERATION)
            cc$acceleration = motionProfile;
    }

    @Override public TrainMotionProfile cc$getTopSpeed() { return cc$topSpeed; }
    @Override public TrainMotionProfile cc$getAcceleration() { return cc$acceleration; }
    @Override public TrainDisassemblyLock cc$getLock() { return cc$disassemblyLock; }
    @Override public int cc$getFuelMultiplier() { return cc$fuelMultiplier; }

}
