package dev.sleepy_evelyn.create_configured.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.trains.station.*;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.widget.IconButton;
import dev.sleepy_evelyn.create_configured.client.CreateConfiguredClient;
import dev.sleepy_evelyn.create_configured.mixin_interfaces.GuiTaggable;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StationScreen.class)
public abstract class StationScreenMixin extends AbstractStationScreen {

    @Unique private static final String CC$DISASSEMBLY_BUTTON_TAG = "disassemble_train";

    @Shadow private IconButton disassembleTrainButton;
    @Unique private IconButton cc$disassemblyLockButton;

    public StationScreenMixin(StationBlockEntity be, GlobalStation station) {
        super(be, station);
    }

    @Inject(method = "init()V", at = @At("TAIL"))
    private void StationScreen(CallbackInfo ci) {
        ((GuiTaggable) disassembleTrainButton).cc$setTag(CC$DISASSEMBLY_BUTTON_TAG);

        /*cc$disassemblyLockButton = new IconButton(guiLeft + 52, guiTop + 65, AllGuiTextures.STOCK_KEEPER_REQUEST_LOCKED);
        addRenderableWidget(cc$disassemblyLockButton);*/
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
