package dev.sleepy_evelyn.create_configured.compat.opac;

import net.minecraft.server.level.ServerPlayer;
import xaero.pac.common.server.api.OpenPACServerAPI;

import java.util.Optional;
import java.util.UUID;

public class OPACImpl implements OPACWrapper {

    @Override
    public Optional<OPACMemberRank> getMemberRank(ServerPlayer player, UUID targetPlayer) {
        var api = OpenPACServerAPI.get(player.server);
        var partyApi = api.getPartyManager().getPartyByMember(player.getUUID());

        if (partyApi != null) {
            var memberInfo = partyApi.getMemberInfo(targetPlayer);

            if (memberInfo != null) {
                return switch (memberInfo.getRank()) {
                    case MEMBER -> Optional.of(OPACMemberRank.MEMBER);
                    case MODERATOR -> Optional.of(OPACMemberRank.MODERATOR);
                    case ADMIN -> Optional.of(OPACMemberRank.ADMIN);
                };
            }
        }
        return Optional.empty();
    }
}
