package dev.sleepy_evelyn.create_configured.mixin.server;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.TrackGraph;
import dev.sleepy_evelyn.create_configured.TrainDisassemblyLock;
import dev.sleepy_evelyn.create_configured.config.CCConfigs;
import dev.sleepy_evelyn.create_configured.mixin_interfaces.server.DisassemblyLockable;
import net.createmod.catnip.data.Pair;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.UUID;

@Mixin(value = Train.class, remap = false, priority = 10000)
public class TrainMixin implements DisassemblyLockable {

    @Unique
    private TrainDisassemblyLock cc$disassemblyLock = TrainDisassemblyLock.NOT_LOCKED;

    @WrapOperation(
            method = "collideWithOtherTrains",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/content/trains/entity/Train;findCollidingTrain(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/resources/ResourceKey;)Lnet/createmod/catnip/data/Pair;"
            )
    )
    private Pair<Train, Vec3> removeTrainCollisions(Train instance, Level otherLeading, Vec3 otherTrailing, Vec3 otherDimension, ResourceKey<Level> start2, Operation<Pair<Train, Vec3>> original) {
        return (CCConfigs.server().trainCollisions.get()) ?
                original.call(instance, otherLeading, otherTrailing, otherDimension, start2) : null;
    }

    @Inject(method = "read", at = @At("TAIL"))
    private static void read(CompoundTag tag, HolderLookup.Provider registries, Map<UUID, TrackGraph> trackNetworks, DimensionPalette dimensions, CallbackInfoReturnable<Train> cir, @Local Train train) {
        if (tag.contains("DisassemblyLock"))
            ((DisassemblyLockable) train).cc$setLock(TrainDisassemblyLock.BY_ID.apply(tag.getInt("DisassemblyLock")));
    }

    @Inject(method = "write", at = @At("RETURN"))
    private void write(DimensionPalette dimensions, HolderLookup.Provider registries, CallbackInfoReturnable<CompoundTag> cir) {
        var tag = cir.getReturnValue();
        tag.putInt("DisassemblyLock", cc$disassemblyLock.getId());
    }

    @Override
    public TrainDisassemblyLock cc$getLock() {
        return cc$disassemblyLock;
    }

    @Override
    public void cc$setLock(TrainDisassemblyLock lock) {
        cc$disassemblyLock = lock;
    }
}
