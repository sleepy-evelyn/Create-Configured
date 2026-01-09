package dev.sleepy_evelyn.create_configured;

import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;

@EventBusSubscriber(modid = CreateConfigured.MOD_ID)
public final class CCPermissionNodes {

    @SuppressWarnings("unchecked")
    public static final PermissionNode<Boolean> BYPASS_TRAIN_DISASSEMBLY = new PermissionNode<>(
            CreateConfigured.MOD_ID, "bypass_train_disassembly", PermissionTypes.BOOLEAN,
            (player, uuid, ctx) -> false
    ).setInformation(
            Component.translatable("create_configured.permission.bypass_train_disassembly.title"),
            Component.translatable("create_configured.permission.bypass_train_disassembly.description")
    );

    @SubscribeEvent
    public static void onRegisterPermissionNode(PermissionGatherEvent.Nodes e) {
        e.addNodes(BYPASS_TRAIN_DISASSEMBLY);
    }
}
