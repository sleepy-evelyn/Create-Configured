package dev.sleepy_evelyn.create_configured.utils;

import dev.sleepy_evelyn.create_configured.CreateConfigured;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

@EventBusSubscriber
public class UnfillableItemsCache extends TickedCacheSet<ResourceLocation> {

    public static final UnfillableItemsCache INSTANCE = new UnfillableItemsCache();

    private UnfillableItemsCache() {
        super(1000, 20 * 60 * 5);
    }

    @SubscribeEvent
    public static void onRecipesReload(AddReloadListenerEvent e) {
        INSTANCE.clearAndReset();
    }
}
