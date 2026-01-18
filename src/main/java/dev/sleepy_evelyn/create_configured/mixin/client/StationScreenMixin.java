package dev.sleepy_evelyn.create_configured.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.trains.station.*;
import com.simibubi.create.foundation.gui.widget.IconButton;
import dev.sleepy_evelyn.create_configured.TrainDisassemblyLock;
import dev.sleepy_evelyn.create_configured.config.CCConfigs;
import dev.sleepy_evelyn.create_configured.gui.StationScreenSynced;
import dev.sleepy_evelyn.create_configured.mixin_interfaces.client.GuiTaggable;
import dev.sleepy_evelyn.create_configured.mixin_interfaces.client.DisassemblyLockSynced;
import dev.sleepy_evelyn.create_configured.network.c2s.ChangeDisassemblyLockPayload;
import dev.sleepy_evelyn.create_configured.network.c2s.NotifyTrainAtStation;
import dev.sleepy_evelyn.create_configured.utils.ScreenUtils;
import dev.sleepy_evelyn.create_configured.utils.SoundUtils;
import net.minecraft.ChatFormatting;
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

import java.util.List;

import static dev.sleepy_evelyn.create_configured.CreateConfiguredClient.isInSinglePlayer;

@Mixin(value = StationScreen.class, remap = false)
public abstract class StationScreenMixin extends AbstractStationScreen implements DisassemblyLockSynced {

    @Unique private static final String CC$DISASSEMBLY_BUTTON_TAG = "disassemble_train";

    @Unique private int cc$sidebarTopX, cc$sidebarTopY;
    @Unique private boolean cc$showLockButton = false, cc$syncedTrainInfo = false, cc$wasTrainPresent = false;
    @Unique private StationScreenSynced cc$synced = new StationScreenSynced();

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
            cc$showLockButton = false;
        else {
            if (!cc$wasTrainPresent)
                PacketDistributor.sendToServer(new NotifyTrainAtStation(blockEntity.getBlockPos()));
            else if (cc$syncedTrainInfo)
                cc$showLockButton = cc$isTrainOwner();
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
        if (!cc$showLockButton) return;
        var lock = cc$synced.getLock();
        var trainSpeed = cc$synced.getTrainSpeed();

        cc$sidebarTopX = guiLeft + 173;
        cc$sidebarTopY = guiTop + 42;

        lock.getTexture().render(graphics, cc$sidebarTopX, cc$sidebarTopY);
        trainSpeed.getTexture().render(graphics, cc$sidebarTopX, cc$sidebarTopY + 18);

        if (mouseX > cc$sidebarTopX && mouseX <= cc$sidebarTopX + 15) {
            if (mouseY > cc$sidebarTopY && mouseY <= cc$sidebarTopY + 15) {
                graphics.renderComponentTooltip(font,
                        List.of(
                                lock.getTooltipComponent("title", ChatFormatting.WHITE),
                                lock.getTooltipComponent("description", ChatFormatting.GRAY),
                                ScreenUtils.Tooltip.switchStateComponent()
                        ), mouseX, mouseY
                );
            } else if (mouseY > cc$sidebarTopY + 18 && mouseY <= cc$sidebarTopY + 33) {
                graphics.renderComponentTooltip(font,
                        List.of(
                                trainSpeed.getTooltipNameComponent(),
                                Component.translatable("create_configured.gui.station.train_speed.blocks_per_second",
                                        trainSpeed.getBlocksPerSecondSpeed()).withStyle(ChatFormatting.GRAY),
                                ScreenUtils.Tooltip.switchStateComponent()
                        ), mouseX, mouseY);
            }
        }
    }

    @Inject(method = "mouseClicked(DDI)Z", at = @At("HEAD"), cancellable = true)
    private void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (!cc$showLockButton) return;
        if (mouseX > cc$sidebarTopX && mouseX <= cc$sidebarTopX + 15) {
            boolean buttonPressed = false;

            if (mouseY > cc$sidebarTopY && mouseY <= cc$sidebarTopY + 15) {
                buttonPressed = true;
                cc$synced.cycleLock();
                PacketDistributor.sendToServer(
                        new ChangeDisassemblyLockPayload(blockEntity.getBlockPos(), cc$synced.getLock()));
            } else if (mouseY > cc$sidebarTopY + 18 && mouseY <= cc$sidebarTopY + 33) {
                buttonPressed = true;
                cc$synced.cycleTrainSpeed();
                // TODO - Speed button packet
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
        original.call(instance, (!cc$isDisassembleButton(instance) || cc$synced.canPlayerDisassemble()) && newValue);
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
            original.call(instance, cc$synced.canPlayerDisassemble() ? newTooltip :
                    Component.translatable("create_configured.message.train.disassembly_denied"));
        else
            original.call(instance, newTooltip);
    }

    @Unique
    public void cc$onSyncDisassemblyLock(boolean canPlayerDisassemble, TrainDisassemblyLock lock) {
        cc$showLockButton = !isInSinglePlayer() && CCConfigs.server().lockTrainDisassembly.get() && cc$isTrainOwner();
        cc$synced = new StationScreenSynced(canPlayerDisassemble, lock);
        cc$syncedTrainInfo = true;
    }

    @Unique
    @SuppressWarnings("DataFlowIssue")
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
