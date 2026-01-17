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
import dev.sleepy_evelyn.create_configured.utils.SoundHelper;
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

@Mixin(StationScreen.class)
public abstract class StationScreenMixin extends AbstractStationScreen implements DisassemblyLockSynced {

    @Unique private static final String CC$DISASSEMBLY_BUTTON_TAG = "disassemble_train";

    @Unique private int cc$lockX, cc$lockY;
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
        var syncedLock = cc$synced.getLock();

        cc$lockX = guiLeft + 173;
        cc$lockY = guiTop + 42;
        syncedLock.getTexture().render(graphics, cc$lockX, cc$lockY);

        if (mouseX > cc$lockX && mouseY > cc$lockY && mouseX <= cc$lockX + 15 && mouseY <= cc$lockY + 15) {
            graphics.renderComponentTooltip(font,
                    List.of(
                            syncedLock.getTooltipComponent("title", ChatFormatting.WHITE),
                            syncedLock.getTooltipComponent("description", ChatFormatting.GRAY),
                            Component.translatable("create_configured.gui.tooltip.switch_state")
                                    .withStyle(ChatFormatting.DARK_GRAY)
                                    .withStyle(ChatFormatting.ITALIC)
                    ), mouseX, mouseY
            );
        }
    }

    @Inject(method = "mouseClicked(DDI)Z", at = @At("HEAD"), cancellable = true)
    private void mouseClicked(double pMouseX, double pMouseY, int pButton, CallbackInfoReturnable<Boolean> cir) {
        if (!cc$showLockButton) return;
        if (pMouseX > cc$lockX && pMouseY > cc$lockY && pMouseX <= cc$lockX + 15 && pMouseY <= cc$lockY + 15) {
            cc$synced.cycleLock();
            SoundHelper.playButtonPress();
            PacketDistributor.sendToServer(
                    new ChangeDisassemblyLockPayload(blockEntity.getBlockPos(), cc$synced.getLock()));
            cir.setReturnValue(true);
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
