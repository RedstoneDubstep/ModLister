package redstonedubstep.mods.modlistobserver;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;

public class ModListCommand {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("modlist").requires(player -> player.hasPermission(3))
				.executes(ctx -> getModList(ctx, ctx.getSource().getPlayerOrException().getGameProfile(), false))
				.then(Commands.argument("target", GameProfileArgument.gameProfile())
						.executes(ctx -> getModList(ctx, GameProfileArgument.getGameProfiles(ctx, "target"), false))
						.then(Commands.literal("all")
								.executes(ctx -> getModList(ctx, GameProfileArgument.getGameProfiles(ctx, "target"), true)))
						.then(Commands.literal("current")
								.executes(ctx -> getModList(ctx, GameProfileArgument.getGameProfiles(ctx, "target"), false)))));
	}

	private static int getModList(CommandContext<CommandSourceStack> ctx, GameProfile target, boolean allMods) {
		return getModList(ctx, Collections.singleton(target), allMods);
	}

	private static int getModList(CommandContext<CommandSourceStack> ctx, Collection<GameProfile> targets, boolean allMods) {
		Set<String> modList = new HashSet<>();
		TranslatableComponent message;

		for (GameProfile target : targets) {
			modList.addAll(allMods ? ModListObserver.getAllSessionMods(target) : ModListObserver.getCurrentMods(target));
		}

		if (targets.size() == 1) {
			GameProfile target = targets.iterator().next();

			if (modList.isEmpty())
				message = new TranslatableComponent("No mods of target %s were found", target.getName());
			else
				message = new TranslatableComponent(allMods ? "All mods target %1$s joined the server with since the last server restart: %2$s" : "The mods target %1$s last joined the server with: %2$s", target.getName(), ComponentUtils.formatList(modList));
		}
		else if (modList.isEmpty())
			message = new TranslatableComponent("No mods of the %s targets were found", targets.size());
		else
			message = new TranslatableComponent(allMods ? "All mods the %1$s targets joined the server with since the last server restart: %2$s" : "The mods the %1$s targets last joined the server with: %2$s", targets.size(), ComponentUtils.formatList(modList));

		if (ctx.getSource().getEntity() instanceof ServerPlayer player)
			player.sendMessage(message, player.getUUID());
		else
			ctx.getSource().sendSuccess(message, false);

		return modList.size();
	}
}
