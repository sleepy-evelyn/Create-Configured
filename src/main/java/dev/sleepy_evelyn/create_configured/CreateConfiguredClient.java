package dev.sleepy_evelyn.create_configured;

import com.simibubi.create.content.trains.schedule.ScheduleItem;
import dev.sleepy_evelyn.create_configured.gui.LoopingScheduleOverlay;
import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

import static dev.sleepy_evelyn.create_configured.CreateConfigured.rl;

@Mod(value = CreateConfigured.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT)
public class CreateConfiguredClient {

    public static String groupsProviderId = "none";
    private static boolean inSinglePlayer = true;

    public CreateConfiguredClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, (_container, screen)
                -> new BaseConfigScreen(screen, _container.getModId()));
    }

    @SubscribeEvent
    public static void onPlayerJoin(ClientPlayerNetworkEvent.LoggingIn e) {
        inSinglePlayer = Minecraft.getInstance().getSingleplayerServer() != null;
    }

    @SubscribeEvent
    public static void onGuiTick(RenderGuiEvent.Post e) {
        LoopingScheduleOverlay.OVERLAY.onGuiTick();
    }

    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiLayersEvent e) {
        e.registerAbove(VanillaGuiLayers.HOTBAR, rl("looping_schedule_request"), LoopingScheduleOverlay.OVERLAY);
    }

    public static boolean isInSinglePlayer() {
        return inSinglePlayer;
    }
}
