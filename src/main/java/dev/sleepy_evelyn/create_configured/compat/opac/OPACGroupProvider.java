package dev.sleepy_evelyn.create_configured.compat.opac;

import dev.sleepy_evelyn.create_configured.groups.GroupsProvider;
import dev.sleepy_evelyn.create_configured.groups.MemberRank;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import xaero.pac.common.server.api.OpenPACServerAPI;

import java.util.Optional;
import java.util.UUID;

public class OPACGroupProvider implements GroupsProvider {

    @Override
    public String id() { return "opac"; }

    @Override
    public Optional<MemberRank> getMemberRank(Player player, UUID targetPlayer) {
        if (player.level().isClientSide) return Optional.empty();

        var serverPlayer = (ServerPlayer) player;
        var api = OpenPACServerAPI.get(serverPlayer.server);
        var partyApi = api.getPartyManager().getPartyByMember(player.getUUID());

        if (partyApi != null) {
            var memberInfo = partyApi.getMemberInfo(targetPlayer);

            if (memberInfo != null) {
                return switch (memberInfo.getRank()) {
                    case MEMBER -> Optional.of(MemberRank.MEMBER);
                    case MODERATOR -> Optional.of(MemberRank.MODERATOR);
                    case ADMIN -> Optional.of(MemberRank.ADMIN);
                };
            }
        }
        return Optional.empty();
    }
}
