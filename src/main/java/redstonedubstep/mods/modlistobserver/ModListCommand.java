package redstonedubstep.mods.modlistobserver;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.GameProfileArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

public class ModListCommand {
	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("modlist").requires(player -> player.hasPermission(3)).executes(ctx -> getModList(ctx, ctx.getSource().getPlayerOrException().getGameProfile(), false))
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
		int success = 0;

		for (GameProfile target : targets) {
			Set<String> modList = allMods ? ModListObserver.getAllMods(target) : ModListObserver.getCurrentMods(target);
			TranslationTextComponent message;

			if (modList != null) {
				message = new TranslationTextComponent(allMods ? "All the mods of target %1$s they ever joined with: %2$s" : "The mods of target %1$s in their current session: %2$s", target.getName(), String.join(", ", modList));
				success++;
			}
			else
				message = new TranslationTextComponent("No mod list of target %s was found", target.getName());

			if (ctx.getSource().getEntity() instanceof ServerPlayerEntity) {
				ServerPlayerEntity player = (ServerPlayerEntity)ctx.getSource().getEntity();

				player.sendMessage(message, player.getUUID());
			}
			else
				ctx.getSource().sendSuccess(message, false);
		}

		return success;
	}
}
