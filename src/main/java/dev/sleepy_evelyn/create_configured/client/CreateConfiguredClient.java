package dev.sleepy_evelyn.create_configured.client;

import dev.sleepy_evelyn.create_configured.CreateConfigured;
import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = CreateConfigured.MOD_ID, dist = Dist.CLIENT)
public class CreateConfiguredClient {

    public static boolean canBypassTrainDisassembly = true;

    public CreateConfiguredClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, (_container, screen)
                -> new BaseConfigScreen(screen, _container.getModId()));
    }
}
