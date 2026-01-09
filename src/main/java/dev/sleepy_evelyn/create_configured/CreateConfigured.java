package dev.sleepy_evelyn.create_configured;

import com.mojang.logging.LogUtils;
import dev.sleepy_evelyn.create_configured.compat.grieflogger.GriefLoggerImpl;
import dev.sleepy_evelyn.create_configured.compat.Mods;
import dev.sleepy_evelyn.create_configured.compat.opac.OPACImpl;
import dev.sleepy_evelyn.create_configured.compat.opac.OPACWrapper;
import dev.sleepy_evelyn.create_configured.config.CCConfigs;
import dev.sleepy_evelyn.create_configured.compat.grieflogger.GriefLoggerWrapper;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Optional;

import static dev.sleepy_evelyn.create_configured.CreateConfigured.MOD_ID;

@Mod(MOD_ID)
public class CreateConfigured {

    public static final String MOD_ID = "create_configured";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static @Nullable GriefLoggerWrapper griefLogger;
    private static @Nullable OPACWrapper opac;

    public CreateConfigured(ModContainer container) {
        CCConfigs.register(container);
        if (Mods.GRIEFLOGGER.isLoaded()) griefLogger = new GriefLoggerImpl();
        if (Mods.OPENPARTIESANDCLAIMS.isLoaded()) opac = new OPACImpl();
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static Optional<GriefLoggerWrapper> griefLogger() { return Optional.ofNullable(griefLogger); }
    public static Optional<OPACWrapper> opac() { return Optional.ofNullable(opac); }
}
