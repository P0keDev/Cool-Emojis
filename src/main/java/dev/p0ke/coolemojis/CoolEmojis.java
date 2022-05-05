package dev.p0ke.coolemojis;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.p0ke.coolemojis.config.CoolEmojisConfig;
import dev.p0ke.coolemojis.manager.EmojiMapManager;
import dev.p0ke.coolemojis.manager.PackManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoolEmojis implements ModInitializer {

	public static final String MOD_ID = "cool-emojis";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// load config & emoji map
		new CoolEmojisConfig().load();
		EmojiMapManager.setupEmojiMapFile();

		// handle commands
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> registerCommands(dispatcher));
	}

	private void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
		// reload command
		dispatcher.register(CommandManager.literal("reloademojis")
				.requires(source -> source.hasPermissionLevel(4)) // op-only
				.executes(ctx -> {
					EmojiMapManager.loadEmojis();
					ctx.getSource().sendFeedback(new LiteralText("Reloaded emojis!"), false);
					return 1;
				}));

		// pack generator command
		dispatcher.register(CommandManager.literal("generateemojipack")
				.requires(source -> source.hasPermissionLevel(4))
				.executes(ctx -> {
					boolean success = PackManager.generatePack();
					if (success)
						ctx.getSource().sendFeedback(new LiteralText("Generated emoji pack!"), false);
					else
						ctx.getSource().sendError(new LiteralText("Failed to generate pack!"));
					return success ? 1 : -1;
				}));

		// register list command if enabled in config
		if (CoolEmojisConfig.listCommandEnabled()) {
			LiteralCommandNode<ServerCommandSource> listNode = dispatcher.register(CommandManager.literal("emojilist")
					.executes(ctx -> {
						ctx.getSource().sendFeedback(new LiteralText("Available emojis:\n" +
								EmojiMapManager.getEmojiList()), false);
						return 1;
					}));
			// work-around for stupid mojang bug
			dispatcher.register(CommandManager.literal("elist").executes(listNode.getCommand()));
			dispatcher.register(CommandManager.literal("emojis").executes(listNode.getCommand()));
		}
	}
}
