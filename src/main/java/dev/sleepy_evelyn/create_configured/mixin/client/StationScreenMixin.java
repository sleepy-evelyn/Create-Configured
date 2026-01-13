package dev.sleepy_evelyn.create_configured.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.trains.station.*;
import com.simibubi.create.foundation.gui.widget.IconButton;
import dev.sleepy_evelyn.create_configured.client.CCGuiTextures;
import dev.sleepy_evelyn.create_configured.CreateConfiguredClient;
import dev.sleepy_evelyn.create_configured.TrainDisassemblyLock;
import dev.sleepy_evelyn.create_configured.mixin_interfaces.GuiTaggable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(StationScreen.class)
public abstract class StationScreenMixin extends AbstractStationScreen {

    @Unique private static final String CC$DISASSEMBLY_BUTTON_TAG = "disassemble_train";

    @Unique private int cc$lockX, cc$lockY;
    @Unique private TrainDisassemblyLock cc$disassemblyLock = TrainDisassemblyLock.NOT_LOCKED;
    @Shadow private IconButton disassembleTrainButton;

    public StationScreenMixin(StationBlockEntity be, GlobalStation station) {
        super(be, station);
    }

    @Inject(method = "init()V", at = @At("TAIL"))
    private void StationScreen(CallbackInfo ci) {
        ((GuiTaggable) disassembleTrainButton).cc$setTag(CC$DISASSEMBLY_BUTTON_TAG);
    }

    @Inject(
            method = "renderWindow(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;renderItem(Lnet/minecraft/world/item/ItemStack;II)V"
            )
    )
    private void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        cc$lockX = guiLeft + 172;
        cc$lockY = guiTop + 42;

        (switch (cc$disassemblyLock) {
            case NOT_LOCKED -> CCGuiTextures.TRAIN_DISASSEMBLY_LOCK_OPEN;
            case LOCKED -> CCGuiTextures.TRAIN_DISASSEMBLY_LOCK_CLOSED;
            case PARTY_MEMBERS_ONLY -> CCGuiTextures.TRAIN_DISASSEMBLY_LOCK_WARN;
        }).render(graphics, cc$lockX, cc$lockY);

        if (mouseX > cc$lockX && mouseY > cc$lockY && mouseX <= cc$lockX + 15 && mouseY <= cc$lockY + 15) {
            graphics.renderComponentTooltip(font,
                    List.of(
                            cc$getLockTooltipComponent("title", ChatFormatting.WHITE),
                            cc$getLockTooltipComponent("description", ChatFormatting.GRAY),
                            Component.translatable("create_configured.gui.tooltip.switch_state")
                                    .withStyle(ChatFormatting.DARK_GRAY)
                                    .withStyle(ChatFormatting.ITALIC)
                    ), mouseX, mouseY
            );
        }
    }

    @Inject(method = "mouseClicked(DDI)Z", at = @At("HEAD"), cancellable = true)
    private void mouseClicked(double pMouseX, double pMouseY, int pButton, CallbackInfoReturnable<Boolean> cir) {
        if (pMouseX > cc$lockX && pMouseY > cc$lockY && pMouseX <= cc$lockX + 15 && pMouseY <= cc$lockY + 15) {
            boolean hasGroupProvider = !CreateConfiguredClient.groupsProviderId.contains("none");

            this.cc$disassemblyLock = switch (cc$disassemblyLock) {
                case NOT_LOCKED -> hasGroupProvider ? TrainDisassemblyLock.PARTY_MEMBERS_ONLY : TrainDisassemblyLock.LOCKED;
                case PARTY_MEMBERS_ONLY -> TrainDisassemblyLock.LOCKED;
                case LOCKED -> TrainDisassemblyLock.NOT_LOCKED;
            };
            Minecraft.getInstance().getSoundManager()
                    .play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(), 1, 1));

            // TODO - Send info to the server
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
        original.call(instance, (!cc$isDisassembleButton(instance) || cc$canDisassemble()) && newValue);
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
            original.call(instance, cc$canDisassemble() ? newTooltip :
                    Component.translatable("create_configured.message.train_disassembly_denied"));
        else
            original.call(instance, newTooltip);
    }

    @Unique
    private Component cc$getLockTooltipComponent(String suffix, ChatFormatting... style) {
        return Component.translatable("create_configured.gui.station.disassembly_lock." +
                switch(cc$disassemblyLock) {
                    case LOCKED -> "locked." + suffix;
                    case NOT_LOCKED -> "not_locked." + suffix;
                    case PARTY_MEMBERS_ONLY -> "party_members." + suffix;
                }
        ).withStyle(style);
    }

    @Unique
    @SuppressWarnings("DataFlowIssue")
    private boolean cc$canDisassemble() {
        if (CreateConfiguredClient.canBypassTrainDisassembly) return true;
        else if (displayedTrain.get() != null) {
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
