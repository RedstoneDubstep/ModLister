package redstonedubstep.mods.modlistobserver;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;

public class ModListCommand {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("modlist").requires(player -> player.hasPermission(3)).executes(ctx -> getModList(ctx, ctx.getSource().getPlayerOrException().getGameProfile(), false))
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
		int success = 0;

		for (GameProfile target : targets) {
			Set<String> modList = allMods ? ModListObserver.getAllMods(target) : ModListObserver.getCurrentMods(target);
			TranslatableComponent message;

			if (modList != null) {
				message = new TranslatableComponent(allMods ? "All the mods of target %1$s they ever joined with: %2$s" : "The mods of target %1$s in their current session: %2$s", target.getName(), String.join(", ", modList));
				success++;
			}
			else
				message = new TranslatableComponent("No mod list of target %s was found", target.getName());

			if (ctx.getSource().getEntity() instanceof ServerPlayer player)
				player.sendMessage(message, player.getUUID());
			else
				ctx.getSource().sendSuccess(message, false);
		}

		return success;
	}
}
