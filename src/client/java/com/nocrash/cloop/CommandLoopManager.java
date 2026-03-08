package com.nocrash.cloop;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public final class CommandLoopManager {
	private static final List<LoopEntry> LOOPS = new ArrayList<>();
	private static int nextId = 1;

	private static List<String> tabSuggestions = Collections.emptyList();
	private static int tabSuggestionIndex = -1;
	private static String lastTabCompletion;

	private CommandLoopManager() {
	}

	public static void tick(MinecraftClient client) {
		ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
		if (networkHandler == null) {
			return;
		}

		long now = System.currentTimeMillis();
		for (LoopEntry entry : LOOPS) {
			if (!entry.running) {
				continue;
			}

			try {
				networkHandler.sendChatCommand(entry.command);
				entry.executions++;
			} catch (Throwable throwable) {
				entry.pause(now);
				if (!entry.hidden) {
					sendLocal(client, "Loop #" + entry.id + " stopped (send failed).");
				}
			}
		}
	}

	public static boolean handleInput(MinecraftClient client, String rawMessage) {
		String message = rawMessage == null ? "" : rawMessage.trim();
		String lower = message.toLowerCase(Locale.ROOT);
		if (!lower.equals(".cloop") && !lower.startsWith(".cloop ")) {
			return false;
		}

		resetTabCompletion();

		if (message.equalsIgnoreCase(".cloop")) {
			sendUsage(client);
			return true;
		}

		String args = message.substring(6).trim();
		if (args.isEmpty()) {
			sendUsage(client);
			return true;
		}

		String[] split = args.split("\\s+", 2);
		String subCommand = split[0].toLowerCase(Locale.ROOT);
		String remainder = split.length > 1 ? split[1].trim() : "";

		switch (subCommand) {
			case "run" -> handleRun(client, remainder);
			case "list" -> handleList(client);
			case "stop" -> handleStopResume(client, remainder, false);
			case "resume" -> handleStopResume(client, remainder, true);
			case "hide" -> handleHideShow(client, remainder, true);
			case "show" -> handleHideShow(client, remainder, false);
			default -> sendUsage(client);
		}

		return true;
	}

	public static String completeTab(String input) {
		if (input == null || !input.startsWith(".")) {
			resetTabCompletion();
			return null;
		}

		if (lastTabCompletion != null && input.equals(lastTabCompletion) && !tabSuggestions.isEmpty()) {
			tabSuggestionIndex = (tabSuggestionIndex + 1) % tabSuggestions.size();
			lastTabCompletion = tabSuggestions.get(tabSuggestionIndex);
			return lastTabCompletion;
		}

		List<String> suggestions = buildSuggestions(input);
		if (suggestions.isEmpty()) {
			resetTabCompletion();
			return null;
		}

		tabSuggestions = suggestions;
		tabSuggestionIndex = 0;
		lastTabCompletion = tabSuggestions.get(0);
		return lastTabCompletion;
	}

	public static List<String> getLiveSuggestions(String input, int maxCount) {
		if (input == null || !input.startsWith(".")) {
			return Collections.emptyList();
		}

		List<String> suggestions = buildSuggestions(input);
		if (suggestions.isEmpty()) {
			return Collections.emptyList();
		}

		int limit = Math.max(1, maxCount);
		if (suggestions.size() <= limit) {
			return suggestions;
		}

		return new ArrayList<>(suggestions.subList(0, limit));
	}

	private static void handleRun(MinecraftClient client, String remainder) {
		if (remainder.isEmpty()) {
			sendLocal(client, "Usage: .cloop run <command>");
			return;
		}

		String command = remainder.startsWith("/") ? remainder.substring(1).trim() : remainder;
		if (command.isEmpty()) {
			sendLocal(client, "Command cannot be empty.");
			return;
		}

		long now = System.currentTimeMillis();
		LoopEntry entry = new LoopEntry(nextId++, command, now, LOOPS.size() + 1);
		LOOPS.add(entry);
		sendLocal(client, "Started loop #" + entry.id + " -> /" + entry.command);
	}

	private static void handleList(MinecraftClient client) {
		if (LOOPS.isEmpty()) {
			sendLocal(client, "No command loops running.");
			return;
		}

		sendLocal(client, "Command loops (oldest first):");
		long now = System.currentTimeMillis();
		for (LoopEntry entry : LOOPS) {
			String runningState = entry.running ? "running" : "paused";
			String visibilityState = entry.hidden ? "hidden" : "shown";
			sendLocal(
				client,
				"#" + entry.id
					+ " order=" + entry.creationOrder
					+ " state=" + runningState
					+ " view=" + visibilityState
					+ " uptime=" + formatDuration(entry.getActiveMillis(now))
					+ " ticks=" + entry.executions
					+ " cmd=/" + entry.command
			);
		}
	}

	private static void handleStopResume(MinecraftClient client, String remainder, boolean resume) {
		if (remainder.isEmpty()) {
			sendLocal(client, "Usage: .cloop " + (resume ? "resume" : "stop") + " <number>");
			return;
		}

		Integer id = parseLoopId(remainder);
		if (id == null) {
			sendLocal(client, "Invalid loop number: " + remainder);
			return;
		}

		LoopEntry entry = findById(id);
		if (entry == null) {
			sendLocal(client, "Loop #" + id + " not found.");
			return;
		}

		long now = System.currentTimeMillis();
		if (resume) {
			if (entry.running) {
				sendLocal(client, "Loop #" + id + " is already running.");
				return;
			}
			entry.resume(now);
			sendLocal(client, "Resumed loop #" + id + ".");
		} else {
			if (!entry.running) {
				sendLocal(client, "Loop #" + id + " is already paused.");
				return;
			}
			entry.pause(now);
			sendLocal(client, "Stopped loop #" + id + ".");
		}
	}

	private static void handleHideShow(MinecraftClient client, String remainder, boolean hide) {
		if (remainder.isEmpty()) {
			sendLocal(client, "Usage: .cloop " + (hide ? "hide" : "show") + " <number|all>");
			return;
		}

		if (remainder.equalsIgnoreCase("all")) {
			int changed = 0;
			for (LoopEntry entry : LOOPS) {
				if (entry.hidden != hide) {
					entry.hidden = hide;
					changed++;
				}
			}
			sendLocal(client, (hide ? "Hidden" : "Shown") + " " + changed + " loop(s).");
			return;
		}

		Integer id = parseLoopId(remainder);
		if (id == null) {
			sendLocal(client, "Invalid loop number: " + remainder);
			return;
		}

		LoopEntry entry = findById(id);
		if (entry == null) {
			sendLocal(client, "Loop #" + id + " not found.");
			return;
		}

		entry.hidden = hide;
		sendLocal(client, (hide ? "Hidden" : "Shown") + " loop #" + id + ".");
	}

	private static List<String> buildSuggestions(String input) {
		String lowerInput = input.toLowerCase(Locale.ROOT);

		if (!lowerInput.startsWith(".cloop")) {
			if (".cloop".startsWith(lowerInput)) {
				return List.of(".cloop ");
			}
			return Collections.emptyList();
		}

		if (lowerInput.equals(".cloop")) {
			return List.of(".cloop ");
		}

		String rest = input.length() > 7 ? input.substring(7) : "";
		if (input.equals(".cloop ") || rest.isBlank()) {
			return subCommandSuggestions("", true);
		}

		String trimmedRest = rest.stripLeading();
		String[] split = trimmedRest.split("\\s+", 2);
		String sub = split[0].toLowerCase(Locale.ROOT);
		boolean hasArg = split.length > 1 || trimmedRest.endsWith(" ");
		String argPrefix = split.length > 1 ? split[1].trim() : "";

		if (!hasArg) {
			return subCommandSuggestions(sub, false);
		}

		if (sub.equals("run")) {
			return Collections.emptyList();
		}

		if (sub.equals("stop") || sub.equals("resume") || sub.equals("hide") || sub.equals("show")) {
			List<String> values = new ArrayList<>();
			for (LoopEntry entry : LOOPS) {
				values.add(Integer.toString(entry.id));
			}

			if (sub.equals("hide") || sub.equals("show")) {
				values.add("all");
			}

			values.sort(Comparator.naturalOrder());

			List<String> suggestions = new ArrayList<>();
			String normalizedPrefix = argPrefix.toLowerCase(Locale.ROOT);
			for (String value : values) {
				if (value.toLowerCase(Locale.ROOT).startsWith(normalizedPrefix)) {
					suggestions.add(".cloop " + sub + " " + value);
				}
			}
			return suggestions;
		}

		return Collections.emptyList();
	}

	private static List<String> subCommandSuggestions(String prefix, boolean includeRunSpacingOnly) {
		String normalized = prefix.toLowerCase(Locale.ROOT);
		List<String> candidates = List.of("run", "list", "stop", "resume", "hide", "show");
		List<String> suggestions = new ArrayList<>();
		for (String candidate : candidates) {
			if (candidate.startsWith(normalized)) {
				if (candidate.equals("list") && !includeRunSpacingOnly) {
					suggestions.add(".cloop " + candidate);
				} else if (candidate.equals("list") && includeRunSpacingOnly) {
					suggestions.add(".cloop " + candidate);
				} else {
					suggestions.add(".cloop " + candidate + " ");
				}
			}
		}
		return suggestions;
	}

	private static void sendUsage(MinecraftClient client) {
		sendLocal(client, "Usage: .cloop run <command>");
		sendLocal(client, "       .cloop list");
		sendLocal(client, "       .cloop stop <number>");
		sendLocal(client, "       .cloop resume <number>");
		sendLocal(client, "       .cloop hide <number|all>");
		sendLocal(client, "       .cloop show <number|all>");
	}

	private static void sendLocal(MinecraftClient client, String message) {
		if (client.player != null) {
			client.player.sendMessage(Text.literal("[NoCrash] " + message), false);
		}
	}

	private static Integer parseLoopId(String value) {
		try {
			return Integer.parseInt(value.trim());
		} catch (NumberFormatException ignored) {
			return null;
		}
	}

	private static LoopEntry findById(int id) {
		for (LoopEntry entry : LOOPS) {
			if (entry.id == id) {
				return entry;
			}
		}
		return null;
	}

	private static String formatDuration(long millis) {
		long seconds = Math.max(0, millis / 1000L);
		long hours = seconds / 3600L;
		long minutes = (seconds % 3600L) / 60L;
		long secs = seconds % 60L;
		return String.format(Locale.ROOT, "%02d:%02d:%02d", hours, minutes, secs);
	}

	private static void resetTabCompletion() {
		tabSuggestions = Collections.emptyList();
		tabSuggestionIndex = -1;
		lastTabCompletion = null;
	}

	private static final class LoopEntry {
		private final int id;
		private final String command;
		private final int creationOrder;
		private boolean running;
		private boolean hidden;
		private long activeStartMs;
		private long accumulatedActiveMs;
		private long executions;

		private LoopEntry(int id, String command, long now, int creationOrder) {
			this.id = id;
			this.command = command;
			this.creationOrder = creationOrder;
			this.running = true;
			this.hidden = false;
			this.activeStartMs = now;
			this.accumulatedActiveMs = 0L;
			this.executions = 0L;
		}

		private void pause(long now) {
			if (!this.running) {
				return;
			}
			this.accumulatedActiveMs += Math.max(0L, now - this.activeStartMs);
			this.running = false;
		}

		private void resume(long now) {
			if (this.running) {
				return;
			}
			this.activeStartMs = now;
			this.running = true;
		}

		private long getActiveMillis(long now) {
			if (!this.running) {
				return this.accumulatedActiveMs;
			}
			return this.accumulatedActiveMs + Math.max(0L, now - this.activeStartMs);
		}
	}
}
