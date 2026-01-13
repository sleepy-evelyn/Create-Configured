package dev.sleepy_evelyn.create_configured.compat.opac;

import dev.sleepy_evelyn.create_configured.groups.GroupsProvider;
import dev.sleepy_evelyn.create_configured.groups.MemberRank;
import net.minecraft.server.MinecraftServer;
import xaero.pac.common.server.api.OpenPACServerAPI;

import java.util.Optional;
import java.util.UUID;

public class OPACGroupProvider implements GroupsProvider {

    @Override
    public String id() { return "opac"; }

    @Override
    public Optional<MemberRank> getMemberRank(MinecraftServer server, UUID player, UUID targetPlayer) {
        if (!server.isDedicatedServer()) return Optional.empty();

        var api = OpenPACServerAPI.get(server);
        var partyApi = api.getPartyManager().getPartyByMember(player);

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
