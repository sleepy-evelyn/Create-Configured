package dev.sleepy_evelyn.create_configured.mixin.create.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.trains.schedule.Schedule;
import com.simibubi.create.content.trains.schedule.ScheduleMenu;
import com.simibubi.create.content.trains.schedule.ScheduleScreen;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;
import dev.sleepy_evelyn.create_configured.mixin.GuiAccessor;
import dev.sleepy_evelyn.create_configured.network.c2s.LoopingScheduleActionPayload;
import dev.sleepy_evelyn.create_configured.trains.LoopingScheduleAction;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ScheduleScreen.class, remap = false)
public abstract class ScheduleScreenMixin extends AbstractSimiContainerScreen<ScheduleMenu> {

    @Shadow private Schedule schedule;

    @Shadow private IconButton cyclicButton;

    public ScheduleScreenMixin(ScheduleMenu container, Inventory inv, Component title) {
        super(container, inv, title);
    }

    @WrapOperation(
            method = "init()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/content/trains/schedule/ScheduleScreen;addRenderableWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;",
                    ordinal = 1
            )
    )
    @SuppressWarnings("DataFlowIssue")
    private <T extends GuiEventListener & Renderable & NarratableEntry> T modifyCyclicButton(ScheduleScreen instance, T cylicWidget, Operation<T> original) {
        if (cylicWidget instanceof IconButton) {
            if (!schedule.cyclic) {
                cyclicButton = new IconButton(leftPos + 21, topPos + 196, AllIcons.I_ACTIVE);
                cyclicButton.setToolTip(Component.translatable("create_configured.gui.schedule.request_repeating_schedule"));
                cyclicButton.withCallback(() -> {
                    cyclicButton.active = false;
                    PacketDistributor.sendToServer(new LoopingScheduleActionPayload(LoopingScheduleAction.START));

                    var gui = minecraft.gui;
                    ((GuiAccessor) gui).cc$setOverlayMessageString(
                            Component.literal("Right click a train to add it to this schedule"));
                    ((GuiAccessor) gui).cc$setOverlayMessageTime(120);
                    minecraft.player.closeContainer();
                });
                return original.call(instance, cyclicButton);
            }
        }
        return original.call(instance, cylicWidget);
    }
}
