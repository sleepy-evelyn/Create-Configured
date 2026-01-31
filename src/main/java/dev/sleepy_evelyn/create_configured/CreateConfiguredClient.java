package dev.sleepy_evelyn.create_configured;

import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

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

    public static boolean isInSinglePlayer() {
        return inSinglePlayer;
    }
}
