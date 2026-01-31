package dev.sleepy_evelyn.create_configured.config;

import dev.sleepy_evelyn.create_configured.CreateConfigured;
import net.createmod.catnip.config.ConfigBase;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

@EventBusSubscriber
public class CCConfigs {

    private static final String CONFIG_ID = CreateConfigured.MOD_ID.replace('_', '-');
    private static final Map<ModConfig.Type, ConfigBase> CONFIGS = new EnumMap<>(ModConfig.Type.class);

    public static final AtomicBoolean RELOADED = new AtomicBoolean();

    private static CCServerConfig serverConfig;
    private static CCSecretsConfig secretsConfig;

    public static CCServerConfig server() {
        return serverConfig;
    }
    public static Optional<CCSecretsConfig> secrets() { return Optional.ofNullable(secretsConfig); }

    public static void register(ModContainer container) {
        serverConfig = register(CCServerConfig::new, ModConfig.Type.SERVER);

        for (Map.Entry<ModConfig.Type, ConfigBase> pair : CONFIGS.entrySet())
            container.registerConfig(pair.getKey(), pair.getValue().specification, getConfigName(pair.getKey()));
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    public static void registerDedicated(ModContainer container) {
        secretsConfig = new CCSecretsConfig();
        container.registerConfig(ModConfig.Type.COMMON, secretsConfig.SPEC, CONFIG_ID + "-secrets.toml");
    }

    private static <T extends ConfigBase> T register(Supplier<T> factory, ModConfig.Type side) {
        Pair<T, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(builder -> {
            T config = factory.get();
            config.registerAll(builder);
            return config;
        });
        T config = specPair.getLeft();
        config.specification = specPair.getRight();
        CONFIGS.put(side, config);
        return config;
    }

    @SubscribeEvent
    public static void onLoad(ModConfigEvent.Loading event) {
        for (ConfigBase config : CONFIGS.values())
            if (config.specification == event.getConfig()
                    .getSpec())
                config.onLoad();
    }

    @SubscribeEvent
    public static void onReload(ModConfigEvent.Reloading event) {
        for (ConfigBase config : CONFIGS.values())
            if (config.specification == event.getConfig().getSpec())
                config.onReload();

        CCConfigs.RELOADED.compareAndSet(false, true);
    }

    private static String getConfigName(ModConfig.Type type) {
        String prefix = CONFIG_ID;

        if (type == ModConfig.Type.SERVER) {
            boolean oldServerConfigExists = FMLPaths.CONFIGDIR.get()
                    .resolve(CreateConfigured.MOD_ID + "-server.toml").toFile().exists();

            if (oldServerConfigExists) prefix = CreateConfigured.MOD_ID;
        }
        return prefix + "-" + type.extension() + ".toml";
    }
}
