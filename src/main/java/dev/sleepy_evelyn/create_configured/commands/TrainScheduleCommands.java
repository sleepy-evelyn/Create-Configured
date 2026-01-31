package dev.sleepy_evelyn.create_configured.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.sleepy_evelyn.create_configured.trains.DiscordSchedule;
import dev.sleepy_evelyn.create_configured.trains.LoopingScheduleAction;
import net.minecraft.commands.CommandSourceStack;

import java.util.List;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class TrainScheduleCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("create")
                .then(literal("train")
                        .requires(cs -> cs.hasPermission(2))
                        .then(literal("schedule")
                                .then(literal("approve")
                                        .then(argument("id", IntegerArgumentType.integer(1))
                                                .executes(ctx ->
                                                        handleScheduleAction(ctx, LoopingScheduleAction.APPROVE))
                                        )
                                )
                                .then(literal("deny")
                                        .then(argument("id", IntegerArgumentType.integer(1))
                                                .executes(ctx ->
                                                        handleScheduleAction(ctx, LoopingScheduleAction.DENY)
                                                )
                                        )
                                )
                                .then(literal("test")
                                        .executes(TrainScheduleCommands::testLoopedScheduleWebhook)
                                )
                        )
                )
        );
    }

    private static int handleScheduleAction(CommandContext<CommandSourceStack> ctx, LoopingScheduleAction strategy) {
        var commandSource = ctx.getSource();
        int scheduleId = IntegerArgumentType.getInteger(ctx, "id");

        return Command.SINGLE_SUCCESS;
    }

    private static int testLoopedScheduleWebhook(CommandContext<CommandSourceStack> ctx) {
        var serverPlayer = ctx.getSource().getPlayer();

        if (serverPlayer == null) return 0;
        var discordSchedule = new DiscordSchedule(serverPlayer, List.of());

        return discordSchedule.send() ? Command.SINGLE_SUCCESS : 0;
    }
}
