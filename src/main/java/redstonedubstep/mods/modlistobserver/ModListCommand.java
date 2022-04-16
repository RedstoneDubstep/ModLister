package redstonedubstep.mods.modlistobserver;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.GameProfileArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TranslationTextComponent;

public class ModListCommand {
	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("modlist").requires(player -> player.hasPermission(3))
				.executes(ctx -> getModList(ctx, ctx.getSource().getPlayerOrException().getGameProfile(), false))
				.then(Commands.argument("target", GameProfileArgument.gameProfile())
						.executes(ctx -> getModList(ctx, GameProfileArgument.getGameProfiles(ctx, "target"), false))
						.then(Commands.literal("all")
								.executes(ctx -> getModList(ctx, GameProfileArgument.getGameProfiles(ctx, "target"), true)))
						.then(Commands.literal("current")
								.executes(ctx -> getModList(ctx, GameProfileArgument.getGameProfiles(ctx, "target"), false)))));
	}

	private static int getModList(CommandContext<CommandSource> ctx, GameProfile target, boolean allMods) {
		return getModList(ctx, Collections.singleton(target), allMods);
	}

	private static int getModList(CommandContext<CommandSource> ctx, Collection<GameProfile> targets, boolean allMods) {
		Set<String> modList = new HashSet<>();
		TranslationTextComponent message;

		for (GameProfile target : targets) {
			modList.addAll(allMods ? ModListObserver.getAllSessionMods(target) : ModListObserver.getCurrentMods(target));
		}

		if (targets.size() == 1) {
			GameProfile target = targets.iterator().next();

			if (modList.isEmpty())
				message = new TranslationTextComponent("No mod list of target %s was found", target.getName());
			else
				message = new TranslationTextComponent(allMods ? "All mods target %1$s joined the server with since the last server restart: %2$s" : "The mods target %1$s last joined the server with: %2$s", target.getName(), TextComponentUtils.formatList(modList));
		}
		else if (modList.isEmpty())
			message = new TranslationTextComponent("No mod lists of the %s targets were found", targets.size());
		else
			message = new TranslationTextComponent(allMods ? "All mods the %1$s targets joined the server with since the last server restart: %2$s" : "The mods the %1$s targets last joined the server with: %2$s", targets.size(), TextComponentUtils.formatList(modList));

		if (ctx.getSource().getEntity() instanceof ServerPlayerEntity)
			ctx.getSource().getEntity().sendMessage(message, ctx.getSource().getEntity().getUUID());
		else
			ctx.getSource().sendSuccess(message, false);

		return modList.size();
	}
}
