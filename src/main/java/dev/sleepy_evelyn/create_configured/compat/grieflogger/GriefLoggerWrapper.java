package dev.sleepy_evelyn.create_configured.compat.grieflogger;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface GriefLoggerWrapper {
    boolean isInspecting(Player player);
    void logBreakBlock(ServerPlayer player, Level level, BlockState state, BlockPos pos);
}
