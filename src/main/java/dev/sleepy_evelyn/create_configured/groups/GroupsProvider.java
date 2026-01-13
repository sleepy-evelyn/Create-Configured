package dev.sleepy_evelyn.create_configured.groups;

import net.minecraft.server.MinecraftServer;

import java.util.Optional;
import java.util.UUID;

public interface GroupsProvider {
    String id();
    Optional<MemberRank> getMemberRank(MinecraftServer server, UUID player, UUID targetPlayer);
}
