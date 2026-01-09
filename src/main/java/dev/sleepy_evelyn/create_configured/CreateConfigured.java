package dev.sleepy_evelyn.create_configured;

import com.mojang.logging.LogUtils;
import dev.sleepy_evelyn.create_configured.compat.GriefLoggerImpl;
import dev.sleepy_evelyn.create_configured.compat.Mods;
import dev.sleepy_evelyn.create_configured.config.CCConfigs;
import dev.sleepy_evelyn.create_configured.compat.GriefLoggerWrapper;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

import static dev.sleepy_evelyn.create_configured.CreateConfigured.MOD_ID;

@Mod(MOD_ID)
public class CreateConfigured {

    public static final String MOD_ID = "create_configured";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static GriefLoggerWrapper griefLogger;

    public CreateConfigured(ModContainer container) {
        CCConfigs.register(container);
        if (Mods.GRIEFLOGGER.isLoaded()) griefLogger = new GriefLoggerImpl();
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static GriefLoggerWrapper griefLogger() { return griefLogger; }
}
