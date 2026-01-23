package dev.sleepy_evelyn.create_configured.permissions;

import dev.sleepy_evelyn.create_configured.CreateConfigured;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;

@EventBusSubscriber(modid = CreateConfigured.MOD_ID)
public final class CCPermissionNodes {

    public static final PermissionNode<Boolean> BYPASS_TRAIN_DISASSEMBLY = booleanNode("bypass_train_disassembly");
    public static final PermissionNode<Boolean> BYPASS_TRAIN_TWEAKS = booleanNode("bypass_train_tweaks");

    @SuppressWarnings("unchecked")
    private static PermissionNode<Boolean> booleanNode(String id) {
        return new PermissionNode<>(CreateConfigured.MOD_ID, id, PermissionTypes.BOOLEAN,
                (player, uuid, ctx) -> false
        ).setInformation(
                Component.translatable("create_configured.permission." + id + ".title"),
                Component.translatable("create_configured.permission." + id + ".description")
        );
    }

    @SubscribeEvent
    public static void onRegisterPermissionNode(PermissionGatherEvent.Nodes e) {
        e.addNodes(BYPASS_TRAIN_DISASSEMBLY, BYPASS_TRAIN_TWEAKS);
    }
}
