package dev.sleepy_evelyn.create_configured.compat.grieflogger;

import com.daqem.grieflogger.event.block.LogBlockEvent;
import com.daqem.grieflogger.model.action.BlockAction;
import com.daqem.grieflogger.player.GriefLoggerServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class GriefLoggerImpl implements GriefLoggerWrapper {

    @Override
    public boolean isInspecting(Player player) {
        return player instanceof GriefLoggerServerPlayer glsp && glsp.grieflogger$isInspecting();
    }

    @Override
    public void logBreakBlock(ServerPlayer player, Level level, BlockState state, BlockPos pos) {
        if (player instanceof GriefLoggerServerPlayer glsp)
            LogBlockEvent.logBlock(glsp, level, state, pos, BlockAction.BREAK_BLOCK);
    }
}
