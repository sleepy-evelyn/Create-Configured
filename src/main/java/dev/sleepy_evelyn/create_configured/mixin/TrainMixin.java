package dev.sleepy_evelyn.create_configured.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.TrackGraph;
import dev.sleepy_evelyn.create_configured.TrainDisassemblyLock;
import dev.sleepy_evelyn.create_configured.TrainMotionProfile;
import dev.sleepy_evelyn.create_configured.mixin_interfaces.server.TrainTweaks;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.fml.loading.FMLEnvironment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mixin(Train.class)
public class TrainMixin implements TrainTweaks {

    @Unique private TrainDisassemblyLock cc$disassemblyLock = TrainDisassemblyLock.NOT_LOCKED;
    @Unique private TrainMotionProfile cc$topSpeed = TrainMotionProfile.DEFAULT_TOP_SPEED;
    @Unique private TrainMotionProfile cc$acceleration = TrainMotionProfile.DEFAULT_ACCELERATION;

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

                disassemblyLockable.cc$setMotionProfile(topSpeedProfile, accelerationProfile);
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
    @Override public void cc$setMotionProfile(TrainMotionProfile topSpeed, TrainMotionProfile acceleration) {
        cc$topSpeed = topSpeed;
        cc$acceleration = acceleration;
    }

    @Override public TrainMotionProfile cc$getTopSpeed() { return cc$topSpeed; }
    @Override public TrainMotionProfile cc$getAcceleration() { return cc$acceleration; }
    @Override public TrainDisassemblyLock cc$getLock() { return cc$disassemblyLock; }
}
