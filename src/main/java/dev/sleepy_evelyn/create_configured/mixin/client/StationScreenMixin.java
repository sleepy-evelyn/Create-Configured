package dev.sleepy_evelyn.create_configured.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.trains.station.*;
import com.simibubi.create.foundation.gui.widget.IconButton;
import dev.sleepy_evelyn.create_configured.TrainDisassemblyLock;
import dev.sleepy_evelyn.create_configured.TrainMotionProfile;
import dev.sleepy_evelyn.create_configured.config.CCConfigs;
import dev.sleepy_evelyn.create_configured.gui.TriStateButton;
import dev.sleepy_evelyn.create_configured.mixin_interfaces.client.GuiTaggable;
import dev.sleepy_evelyn.create_configured.mixin_interfaces.client.TrainTweaksSynced;
import dev.sleepy_evelyn.create_configured.network.c2s.ChangeDisassemblyLockPayload;
import dev.sleepy_evelyn.create_configured.network.c2s.NotifyTrainAtStation;
import dev.sleepy_evelyn.create_configured.permissions.TrainTweakPermissions;
import dev.sleepy_evelyn.create_configured.utils.SoundUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
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

import java.util.*;

import static dev.sleepy_evelyn.create_configured.CreateConfiguredClient.isInSinglePlayer;

@Mixin(value = StationScreen.class, remap = false)
public abstract class StationScreenMixin extends AbstractStationScreen implements TrainTweaksSynced {

    @Unique private static final String CC$DISASSEMBLY_BUTTON_TAG = "disassemble_train";

    @Unique private int cc$sidebarTopX, cc$sidebarTopY;
    @Unique private boolean cc$wasTrainPresent = false, cc$showLock = false, cc$hasSynced = false;

    @Unique private TrainTweakPermissions cc$permissions = new TrainTweakPermissions();
    @Unique private final List<TriStateButton> cc$triStateButtons = new LinkedList<>();
    @Shadow private IconButton disassembleTrainButton;

    public StationScreenMixin(StationBlockEntity be, GlobalStation station) {
        super(be, station);
    }

    @Inject(method = "init()V", at = @At("TAIL"))
    private void StationScreen(CallbackInfo ci) {
        ((GuiTaggable) disassembleTrainButton).cc$setTag(CC$DISASSEMBLY_BUTTON_TAG);
    }

    @Inject(method = "tickTrainDisplay", at = @At("HEAD"))
    private void tickTrainDisplay(CallbackInfo ci) {
        boolean trainPresent = trainPresent();

        if (isInSinglePlayer() || !CCConfigs.server().lockTrainDisassembly.get() || !trainPresent)
            cc$showLock = false;
        else {
            if (!cc$wasTrainPresent) // If a new Train has arrived, request new data.
                PacketDistributor.sendToServer(new NotifyTrainAtStation(blockEntity.getBlockPos()));
            else if (cc$hasSynced) {
                cc$showLock = cc$isTrainOwner();
            }
        }
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
        cc$sidebarTopX = guiLeft + 173;
        cc$sidebarTopY = guiTop + 40;

        int row = 0;
        for (var button : cc$triStateButtons) {
            button.getTexture().render(graphics, cc$sidebarTopX, cc$sidebarTopY + (18 * row));
            row++;
        }
        if (mouseX > cc$sidebarTopX && mouseX <= cc$sidebarTopX + 15) {
            row = 0;
            for (var triStateButton : cc$triStateButtons) {
                if (mouseY > cc$sidebarTopY + (18 * row) && mouseY <= cc$sidebarTopY + (18 * row) + 15) {
                    graphics.renderComponentTooltip(font, triStateButton.getTooltip(), mouseX, mouseY);
                    break;
                }
                row++;
            }
        }
    }

    @Inject(method = "mouseClicked(DDI)Z", at = @At("HEAD"), cancellable = true)
    private void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (cc$triStateButtons.isEmpty()) return;
        if (mouseX > cc$sidebarTopX && mouseX <= cc$sidebarTopX + 15) {
            int row = 0;
            boolean buttonPressed = false;

            for (var triStateButton : cc$triStateButtons) {
                if (mouseY > cc$sidebarTopY + (18 * row) && mouseY <= cc$sidebarTopY + (18 * row) + 15) {
                    if (triStateButton instanceof TrainMotionProfile motionProfile) {
                        cc$triStateButtons.set(row, motionProfile.nextState());
                        // TODO - Send packet back
                    } else if (triStateButton instanceof TrainDisassemblyLock lock) {
                        cc$triStateButtons.set(row, lock.nextState());
                        PacketDistributor.sendToServer(
                                new ChangeDisassemblyLockPayload(blockEntity.getBlockPos(), lock));
                    }
                    buttonPressed = true;
                    break;
                }
                row++;
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
        cc$showLock = !isInSinglePlayer() && CCConfigs.server().lockTrainDisassembly.get() && cc$isTrainOwner();
        cc$permissions = permissions;

        if (cc$showLock) cc$triStateButtons.add(lock);
        if (permissions.canChangeTopSpeed()) cc$triStateButtons.add(topSpeed);
        if (permissions.canChangeAcceleration()) cc$triStateButtons.add(acceleration);
        cc$hasSynced = true;
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
