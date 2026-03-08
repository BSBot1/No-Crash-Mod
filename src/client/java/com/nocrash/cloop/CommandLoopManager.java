package com.nocrash.cloop;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class CommandLoopManager {
	private static final List<LoopEntry> LOOPS = new ArrayList<>();
	private static final int HIDDEN_OUTPUT_BUDGET_LIMIT = 2_000;
	private static final long HIDDEN_OUTPUT_BUDGET_TIMEOUT_MS = 1_500L;
	private static int nextId = 1;
	private static int hiddenOutputBudget;
	private static long lastHiddenCommandAtMs;

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
				if (entry.hidden) {
					noteHiddenCommandSent(now);
				}
			} catch (Throwable throwable) {
				entry.pause(now);
				if (!entry.hidden) {
					sendLocal(client, "Loop #" + entry.id + " paused (send failed).");
				}
			}
		}
	}

	public static int sendUsage(MinecraftClient client) {
		sendLocal(client, "Usage: /cloop run <command>");
		sendLocal(client, "       /cloop list");
		sendLocal(client, "       /cloop pause <number|all>");
		sendLocal(client, "       /cloop stop <number|all>");
		sendLocal(client, "       /cloop resume <number|all>");
		sendLocal(client, "       /cloop hide <number|all>");
		sendLocal(client, "       /cloop show <number|all>");
		return 1;
	}

	public static int runLoop(MinecraftClient client, String remainder) {
		if (remainder == null || remainder.isBlank()) {
			sendLocal(client, "Usage: /cloop run <command>");
			return 0;
		}

		String command = remainder.startsWith("/") ? remainder.substring(1).trim() : remainder.trim();
		if (command.isEmpty()) {
			sendLocal(client, "Command cannot be empty.");
			return 0;
		}

		long now = System.currentTimeMillis();
		LoopEntry entry = new LoopEntry(nextId++, command, now, LOOPS.size() + 1);
		LOOPS.add(entry);
		sendLocal(client, "Started loop #" + entry.id + " -> /" + entry.command);
		return 1;
	}

	public static int listLoops(MinecraftClient client) {
		if (LOOPS.isEmpty()) {
			sendLocal(client, "No command loops running.");
			return 1;
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
		return 1;
	}

	public static int stopLoop(MinecraftClient client, int id) {
		int index = findIndexById(id);
		if (index < 0) {
			sendLocal(client, "Loop #" + id + " not found.");
			return 0;
		}

		LOOPS.remove(index);
		clearSuppressionIfNothingHiddenRunning();
		sendLocal(client, "Stopped and removed loop #" + id + ".");
		return 1;
	}

	public static int stopAllLoops(MinecraftClient client) {
		int removed = LOOPS.size();
		LOOPS.clear();
		clearSuppressionIfNothingHiddenRunning();
		sendLocal(client, "Stopped and removed " + removed + " loop(s).");
		return 1;
	}

	public static int pauseLoop(MinecraftClient client, int id) {
		LoopEntry entry = findById(id);
		if (entry == null) {
			sendLocal(client, "Loop #" + id + " not found.");
			return 0;
		}

		if (!entry.running) {
			sendLocal(client, "Loop #" + id + " is already paused.");
			return 0;
		}

		entry.pause(System.currentTimeMillis());
		clearSuppressionIfNothingHiddenRunning();
		sendLocal(client, "Paused loop #" + id + ".");
		return 1;
	}

	public static int pauseAllLoops(MinecraftClient client) {
		int paused = 0;
		long now = System.currentTimeMillis();
		for (LoopEntry entry : LOOPS) {
			if (!entry.running) {
				continue;
			}
			entry.pause(now);
			paused++;
		}

		clearSuppressionIfNothingHiddenRunning();
		sendLocal(client, "Paused " + paused + " loop(s).");
		return 1;
	}

	public static int resumeLoop(MinecraftClient client, int id) {
		LoopEntry entry = findById(id);
		if (entry == null) {
			sendLocal(client, "Loop #" + id + " not found.");
			return 0;
		}

		if (entry.running) {
			sendLocal(client, "Loop #" + id + " is already running.");
			return 0;
		}

		entry.resume(System.currentTimeMillis());
		sendLocal(client, "Resumed loop #" + id + ".");
		return 1;
	}

	public static int resumeAllLoops(MinecraftClient client) {
		int resumed = 0;
		long now = System.currentTimeMillis();
		for (LoopEntry entry : LOOPS) {
			if (entry.running) {
				continue;
			}
			entry.resume(now);
			resumed++;
		}

		sendLocal(client, "Resumed " + resumed + " loop(s).");
		return 1;
	}

	public static int setHidden(MinecraftClient client, int id, boolean hidden) {
		LoopEntry entry = findById(id);
		if (entry == null) {
			sendLocal(client, "Loop #" + id + " not found.");
			return 0;
		}

		entry.hidden = hidden;
		clearSuppressionIfNothingHiddenRunning();
		sendLocal(client, (hidden ? "Hidden" : "Shown") + " loop #" + id + ".");
		return 1;
	}

	public static int setHiddenAll(MinecraftClient client, boolean hidden) {
		int changed = 0;
		for (LoopEntry entry : LOOPS) {
			if (entry.hidden != hidden) {
				entry.hidden = hidden;
				changed++;
			}
		}

		clearSuppressionIfNothingHiddenRunning();
		sendLocal(client, (hidden ? "Hidden" : "Shown") + " " + changed + " loop(s).");
		return 1;
	}

	public static boolean shouldSuppressHiddenLoopOutput(Text message) {
		if (message == null || hiddenOutputBudget <= 0) {
			return false;
		}

		long now = System.currentTimeMillis();
		if (now - lastHiddenCommandAtMs > HIDDEN_OUTPUT_BUDGET_TIMEOUT_MS) {
			hiddenOutputBudget = 0;
			return false;
		}

		String raw = message.getString();
		if (raw == null || raw.isBlank() || raw.startsWith("[NoCrash]")) {
			return false;
		}

		hiddenOutputBudget--;
		return true;
	}

	public static List<String> getLoopIdSuggestions() {
		List<String> ids = new ArrayList<>();
		for (LoopEntry entry : LOOPS) {
			ids.add(Integer.toString(entry.id));
		}
		return ids;
	}

	private static LoopEntry findById(int id) {
		for (LoopEntry entry : LOOPS) {
			if (entry.id == id) {
				return entry;
			}
		}
		return null;
	}

	private static int findIndexById(int id) {
		for (int i = 0; i < LOOPS.size(); i++) {
			if (LOOPS.get(i).id == id) {
				return i;
			}
		}
		return -1;
	}

	private static void noteHiddenCommandSent(long now) {
		lastHiddenCommandAtMs = now;
		hiddenOutputBudget = Math.min(hiddenOutputBudget + 1, HIDDEN_OUTPUT_BUDGET_LIMIT);
	}

	private static void clearSuppressionIfNothingHiddenRunning() {
		if (hasRunningHiddenLoop()) {
			return;
		}

		hiddenOutputBudget = 0;
	}

	private static boolean hasRunningHiddenLoop() {
		for (LoopEntry entry : LOOPS) {
			if (entry.running && entry.hidden) {
				return true;
			}
		}
		return false;
	}

	private static void sendLocal(MinecraftClient client, String message) {
		if (client.player != null) {
			client.player.sendMessage(Text.literal("[NoCrash] " + message), false);
		}
	}

	private static String formatDuration(long millis) {
		long seconds = Math.max(0, millis / 1000L);
		long hours = seconds / 3600L;
		long minutes = (seconds % 3600L) / 60L;
		long secs = seconds % 60L;
		return String.format(Locale.ROOT, "%02d:%02d:%02d", hours, minutes, secs);
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
