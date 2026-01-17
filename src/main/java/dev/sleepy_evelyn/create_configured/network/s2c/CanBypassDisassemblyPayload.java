package dev.sleepy_evelyn.create_configured.network.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import static dev.sleepy_evelyn.create_configured.CreateConfigured.rl;

public record CanBypassDisassemblyPayload(boolean canBypassTrainDisassembly) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<CanBypassDisassemblyPayload> TYPE =
            new CustomPacketPayload.Type<>(rl("can_bypass_disassembly"));

    public static final StreamCodec<FriendlyByteBuf, CanBypassDisassemblyPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, CanBypassDisassemblyPayload::canBypassTrainDisassembly,
                    CanBypassDisassemblyPayload::new
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
