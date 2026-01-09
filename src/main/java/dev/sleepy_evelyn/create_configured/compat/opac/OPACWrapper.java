package dev.sleepy_evelyn.create_configured.compat.opac;

import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;
import java.util.UUID;

public interface OPACWrapper {
    Optional<OPACMemberRank> getMemberRank(ServerPlayer player, UUID targetPlayer);
}
