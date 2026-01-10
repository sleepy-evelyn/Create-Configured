package dev.sleepy_evelyn.create_configured.groups;

import net.minecraft.world.entity.player.Player;

import java.util.Optional;
import java.util.UUID;

public interface GroupsProvider {
    String id();
    Optional<MemberRank> getMemberRank(Player player, UUID targetPlayer);
}
