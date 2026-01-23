package dev.sleepy_evelyn.create_configured.mixin.server;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.TrackGraph;
import dev.sleepy_evelyn.create_configured.TrainDisassemblyLock;
import dev.sleepy_evelyn.create_configured.TrainMotionProfile;
import dev.sleepy_evelyn.create_configured.config.CCConfigs;
import dev.sleepy_evelyn.create_configured.mixin_interfaces.server.TrainTweaks;
import net.createmod.catnip.data.Pair;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mixin(value = Train.class, remap = false, priority = 10000)
public class TrainMixin {

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
}
