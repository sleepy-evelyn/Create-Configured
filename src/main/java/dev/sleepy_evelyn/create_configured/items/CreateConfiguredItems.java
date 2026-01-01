package dev.sleepy_evelyn.create_configured.items;

import dev.sleepy_evelyn.create_configured.CreateConfigured;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

import static dev.sleepy_evelyn.create_configured.CreateConfigured.rl;

@EventBusSubscriber(modid = CreateConfigured.MOD_ID)
public final class CreateConfiguredItems {

    @SubscribeEvent
    public static void registerItems(RegisterEvent e) {
        e.register(
                Registries.ITEM,
                registry ->
                        registry.register(rl("repeating_schedule"), new RepeatingScheduleItem())
        );
    }
}
