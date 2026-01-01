package dev.sleepy_evelyn.create_configured.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.trains.schedule.Schedule;
import com.simibubi.create.content.trains.schedule.ScheduleMenu;
import com.simibubi.create.content.trains.schedule.ScheduleScreen;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ScheduleScreen.class)
public abstract class ScheduleScreenMixin extends AbstractSimiContainerScreen<ScheduleMenu> {

    @Shadow private Schedule schedule;

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
    private <T extends GuiEventListener & Renderable & NarratableEntry> T cc$modifyCyclicButton(ScheduleScreen instance, T cyclicButton, Operation<T> original) {
        IconButton newCyclicButton;

        if (schedule.cyclic) {
            newCyclicButton = new IconButton(leftPos + 21, topPos + 196, AllIcons.I_REFRESH);
            newCyclicButton.setToolTip(Component.translatable("create_configured.gui.schedule.cyclic_enabled"));
        } else {
            newCyclicButton = new IconButton(leftPos + 21, topPos + 196, AllIcons.I_DISABLE);
            newCyclicButton.setToolTip(Component.translatable("create_configured.gui.schedule.cyclic_disabled"));
        }
        newCyclicButton.active = false;
        return original.call(instance, newCyclicButton);
    }
}
