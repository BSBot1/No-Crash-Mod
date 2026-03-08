package com.nocrash.cloop;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public final class CloopClientCommand {
	private static final SuggestionProvider<FabricClientCommandSource> LOOP_ID_SUGGESTIONS = (context, builder) -> {
		for (String id : CommandLoopManager.getLoopIdSuggestions()) {
			builder.suggest(id);
		}
		return builder.buildFuture();
	};

	private CloopClientCommand() {
	}

	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(literal("cloop")
			.executes(context -> CommandLoopManager.sendUsage(context.getSource().getClient()))
			.then(literal("run")
				.then(argument("command", StringArgumentType.greedyString())
					.executes(context -> CommandLoopManager.runLoop(
						context.getSource().getClient(),
						StringArgumentType.getString(context, "command")
					))
				)
			)
			.then(literal("list")
				.executes(context -> CommandLoopManager.listLoops(context.getSource().getClient()))
			)
			.then(literal("pause")
				.then(literal("all")
					.executes(context -> CommandLoopManager.pauseAllLoops(context.getSource().getClient()))
				)
				.then(argument("number", IntegerArgumentType.integer(1))
					.suggests(LOOP_ID_SUGGESTIONS)
					.executes(context -> CommandLoopManager.pauseLoop(
						context.getSource().getClient(),
						IntegerArgumentType.getInteger(context, "number")
					))
				)
			)
			.then(literal("stop")
				.then(literal("all")
					.executes(context -> CommandLoopManager.stopAllLoops(context.getSource().getClient()))
				)
				.then(argument("number", IntegerArgumentType.integer(1))
					.suggests(LOOP_ID_SUGGESTIONS)
					.executes(context -> CommandLoopManager.stopLoop(
						context.getSource().getClient(),
						IntegerArgumentType.getInteger(context, "number")
					))
				)
			)
			.then(literal("resume")
				.then(literal("all")
					.executes(context -> CommandLoopManager.resumeAllLoops(context.getSource().getClient()))
				)
				.then(argument("number", IntegerArgumentType.integer(1))
					.suggests(LOOP_ID_SUGGESTIONS)
					.executes(context -> CommandLoopManager.resumeLoop(
						context.getSource().getClient(),
						IntegerArgumentType.getInteger(context, "number")
					))
				)
			)
			.then(literal("hide")
				.then(literal("all")
					.executes(context -> CommandLoopManager.setHiddenAll(context.getSource().getClient(), true))
				)
				.then(argument("number", IntegerArgumentType.integer(1))
					.suggests(LOOP_ID_SUGGESTIONS)
					.executes(context -> CommandLoopManager.setHidden(
						context.getSource().getClient(),
						IntegerArgumentType.getInteger(context, "number"),
						true
					))
				)
			)
			.then(literal("show")
				.then(literal("all")
					.executes(context -> CommandLoopManager.setHiddenAll(context.getSource().getClient(), false))
				)
				.then(argument("number", IntegerArgumentType.integer(1))
					.suggests(LOOP_ID_SUGGESTIONS)
					.executes(context -> CommandLoopManager.setHidden(
						context.getSource().getClient(),
						IntegerArgumentType.getInteger(context, "number"),
						false
					))
				)
			)
		);
	}
}
