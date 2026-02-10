package dev.sleepy_evelyn.create_configured.mixin.create.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.trains.station.*;
import com.simibubi.create.foundation.gui.widget.IconButton;
import dev.sleepy_evelyn.create_configured.CreateConfigured;
import dev.sleepy_evelyn.create_configured.trains.TrainDisassemblyLock;
import dev.sleepy_evelyn.create_configured.trains.TrainMotionProfile;
import dev.sleepy_evelyn.create_configured.compat.Mods;
import dev.sleepy_evelyn.create_configured.gui.MultiStateButton;
import dev.sleepy_evelyn.create_configured.mixin_interfaces.client.GuiTaggable;
import dev.sleepy_evelyn.create_configured.mixin_interfaces.client.TrainTweaksSynced;
import dev.sleepy_evelyn.create_configured.mixin_interfaces.server.TrainTweaks;
import dev.sleepy_evelyn.create_configured.network.c2s.ChangeDisassemblyLockPayload;
import dev.sleepy_evelyn.create_configured.network.c2s.ChangeMotionProfilePayload;
import dev.sleepy_evelyn.create_configured.network.c2s.NotifyTrainAtStationPayload;
import dev.sleepy_evelyn.create_configured.permissions.TrainTweakPermissions;
import dev.sleepy_evelyn.create_configured.utils.SoundUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;
import java.util.*;
import java.util.List;

import static dev.sleepy_evelyn.create_configured.CreateConfiguredClient.isInSinglePlayer;

@Mixin(value = StationScreen.class, remap = false)
public abstract class StationScreenMixin extends AbstractStationScreen implements TrainTweaksSynced {

    @Unique private static final String CC$DISASSEMBLY_BUTTON_TAG = "disassemble_train";

    @Unique private int cc$sidebarTopX, cc$sidebarTopY;
    @Unique private boolean cc$wasTrainPresent = false;

    @Unique private TrainTweakPermissions cc$permissions = new TrainTweakPermissions();
    @Unique private final List<MultiStateButton> cc$triStateButtons = new LinkedList<>();
    @Shadow private IconButton disassembleTrainButton;

    public StationScreenMixin(StationBlockEntity be, GlobalStation station) {
        super(be, station);
    }

    @Inject(method = "init()V", at = @At("TAIL"))
    private void StationScreen(CallbackInfo ci) {
        ((GuiTaggable) disassembleTrainButton).cc$setTag(CC$DISASSEMBLY_BUTTON_TAG);

        Mods.RAILWAYS.version()
                .filter(version -> !version.contains("0.1.0-rc") && !version.contains("0.1.0-beta"))
                .ifPresent(v -> cc$removeRailwaysLimitCheckbox());
    }

    @Unique
    private void cc$removeRailwaysLimitCheckbox() {
        try {
            Class<?> mixinClass = this.getClass();
            Field limitCheckboxField = mixinClass.getDeclaredField("railways$limitCheckbox");
            limitCheckboxField.setAccessible(true);

            ((Checkbox) limitCheckboxField.get(this)).visible = false;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            CreateConfigured.logger.error("Failed to remove limit checkbox from {}", Mods.RAILWAYS.modName(), e);
        }
    }

    @Inject(method = "tickTrainDisplay", at = @At("HEAD"))
    private void tickTrainDisplay(CallbackInfo ci) {
        boolean trainPresent = trainPresent();

        if (trainPresent && !cc$wasTrainPresent) // If a new Train has arrived, request new data.
            PacketDistributor.sendToServer(new NotifyTrainAtStationPayload(blockEntity.getBlockPos()));
        else if (!trainPresent && cc$wasTrainPresent)
            cc$triStateButtons.clear();

        cc$wasTrainPresent = trainPresent;
    }

    @Inject(
            method = "renderWindow(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;renderItem(Lnet/minecraft/world/item/ItemStack;II)V"
            )
    )
    private void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (cc$triStateButtons.isEmpty()) return;
        cc$sidebarTopX = guiLeft + 98;
        cc$sidebarTopY = guiTop + 104;

        int column = 0;
        for (var button : cc$triStateButtons) {
            button.getTexture().render(graphics, cc$sidebarTopX + (19 * column), cc$sidebarTopY);
            column++;
        }
        if (mouseY > cc$sidebarTopY && mouseY <= cc$sidebarTopY + 15) {
            column = 0;
            for (var triStateButton : cc$triStateButtons) {
                if (mouseX > cc$sidebarTopX + (19 * column) && mouseX <= cc$sidebarTopX + (19 * column) + 15) {
                    graphics.renderComponentTooltip(font, triStateButton.getTooltip(), mouseX, mouseY);
                    break;
                }
                column++;
            }
        }
    }

    @Inject(method = "mouseClicked(DDI)Z", at = @At("HEAD"), cancellable = true)
    private void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (cc$triStateButtons.isEmpty()) return;
        if (mouseY > cc$sidebarTopY && mouseY <= cc$sidebarTopY + 15) {
            int column = 0;
            boolean buttonPressed = false;

            for (var triStateButton : cc$triStateButtons) {
                if (mouseX > cc$sidebarTopX + (19 * column) && mouseX <= cc$sidebarTopX + (19 * column) + 15) {
                    if (triStateButton instanceof TrainMotionProfile motionProfile) {
                        var nextMotionProfile = motionProfile.nextState();

                        cc$triStateButtons.set(column, nextMotionProfile);
                        if (displayedTrain.get() instanceof TrainTweaks trainTweaks)
                            trainTweaks.cc$setMotionProfile(nextMotionProfile);

                        PacketDistributor.sendToServer(
                                new ChangeMotionProfilePayload(blockEntity.getBlockPos(), nextMotionProfile));
                    } else if (triStateButton instanceof TrainDisassemblyLock lock) {
                        var nextLock = lock.nextState();

                        cc$triStateButtons.set(column, nextLock);
                        PacketDistributor.sendToServer(
                                new ChangeDisassemblyLockPayload(blockEntity.getBlockPos(), nextLock));
                    }
                    buttonPressed = true;
                    break;
                }
                column++;
            }
            if (buttonPressed) {
                SoundUtils.playButtonPress();
                cir.setReturnValue(true);
            }
        }
    }

    @WrapOperation(
            method = "tickTrainDisplay",
            at = @At(
                    value = "FIELD",
                    target = "Lcom/simibubi/create/foundation/gui/widget/IconButton;active:Z",
                    opcode = Opcodes.PUTFIELD
            )
    )
    private void disassembleButtonActiveState(IconButton instance, boolean newValue, Operation<Void> original) {
        original.call(instance, (!cc$isDisassembleButton(instance) || cc$permissions.canDisassemble()) && newValue);
    }

    @WrapOperation(
            method = "updateAssemblyTooltip",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/foundation/gui/widget/IconButton;setToolTip(Lnet/minecraft/network/chat/Component;)V"
            )
    )
    private void disassembleButtonSetTooltip(IconButton instance, Component newTooltip, Operation<Void> original) {
        if (cc$isDisassembleButton(instance))
            original.call(instance, cc$permissions.canDisassemble() ? newTooltip :
                    Component.translatable("create_configured.message.train.disassembly_denied"));
        else
            original.call(instance, newTooltip);
    }

    @Unique @Override
    public void cc$onSync(TrainTweakPermissions permissions, TrainDisassemblyLock lock, TrainMotionProfile topSpeed, TrainMotionProfile acceleration) {
        cc$triStateButtons.clear();
        cc$permissions = permissions;

        if (permissions.canDisassemble() && !isInSinglePlayer() && cc$isTrainOwner()) cc$triStateButtons.add(lock);
        if (permissions.canChangeTopSpeed()) cc$triStateButtons.add(topSpeed);
        if (permissions.canChangeAcceleration()) cc$triStateButtons.add(acceleration);
    }

    @Unique @SuppressWarnings("DataFlowIssue")
    private boolean cc$isTrainOwner() {
        if (displayedTrain.get() != null) {
            var trainOwnerUuid = displayedTrain.get().owner;
            var playerUuid = Minecraft.getInstance().player.getUUID();

            return trainOwnerUuid == null || trainOwnerUuid.equals(playerUuid);
        }
        return true;
    }

    @Unique
    private boolean cc$isDisassembleButton(IconButton button) {
        return ((GuiTaggable) button).cc$matchesTag(CC$DISASSEMBLY_BUTTON_TAG);
    }
}
