package dev.sleepy_evelyn.create_configured;

import com.mojang.logging.LogUtils;
import com.simibubi.create.content.redstone.displayLink.source.StationSummaryDisplaySource;
import dev.sleepy_evelyn.create_configured.config.CCConfigs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

import static dev.sleepy_evelyn.create_configured.CreateConfigured.MOD_ID;

@Mod(MOD_ID)
public class CreateConfigured {

    public static final String MOD_ID = "create_configured";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CreateConfigured(ModContainer container) {
        CCConfigs.register(container);
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static boolean isDedicatedServer(Level level) {
        return level.getServer() != null && level.getServer().isDedicatedServer();
    }

    public static boolean isDedicatedServer() {
        return FMLEnvironment.dist.isDedicatedServer();
    }
}
