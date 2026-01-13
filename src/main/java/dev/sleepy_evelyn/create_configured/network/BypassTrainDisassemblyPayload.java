package dev.sleepy_evelyn.create_configured.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import static dev.sleepy_evelyn.create_configured.CreateConfigured.rl;

public record BypassTrainDisassemblyPayload(boolean canBypassTrainDisassembly) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<BypassTrainDisassemblyPayload> TYPE =
            new CustomPacketPayload.Type<>(rl("bypass_train_disassembly"));

    public static final StreamCodec<FriendlyByteBuf, BypassTrainDisassemblyPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, BypassTrainDisassemblyPayload::canBypassTrainDisassembly,
                    BypassTrainDisassemblyPayload::new
            );

    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
