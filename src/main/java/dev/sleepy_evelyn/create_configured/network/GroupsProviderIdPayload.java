package dev.sleepy_evelyn.create_configured.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import static dev.sleepy_evelyn.create_configured.CreateConfigured.rl;

public record GroupsProviderIdPayload(String providerId) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<GroupsProviderIdPayload> TYPE =
            new CustomPacketPayload.Type<>(rl("groups_provider_id"));

    public static final StreamCodec<FriendlyByteBuf, GroupsProviderIdPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, GroupsProviderIdPayload::providerId,
                    GroupsProviderIdPayload::new
            );

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
