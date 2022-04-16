package redstonedubstep.mods.modlistobserver;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.FMLNetworkConstants;

@Mod(ModListObserver.MODID)
public class ModListObserver {
	public static final String MODID = "modlistobserver";
	private static final Map<GameProfile, Set<String>> ALL_MODS = new HashMap<>();
	private static final Map<GameProfile, Set<String>> CURRENT_MODS = new HashMap<>();
	private static final Logger LOGGER = LogManager.getLogger();

	public ModListObserver() {
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
		MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
	}

	public void registerCommands(RegisterCommandsEvent event) {
		ModListCommand.register(event.getDispatcher());
	}

	public static Set<String> getAllMods(GameProfile player) {
		return ALL_MODS.get(player);
	}

	public static Set<String> getCurrentMods(GameProfile player) {
		return CURRENT_MODS.get(player);
	}

	public static void updateModListOnJoin(Set<String> modList, GameProfile player) {
		CURRENT_MODS.put(player, modList);

		ALL_MODS.putIfAbsent(player, modList);
		ALL_MODS.get(player).addAll(modList);

		LOGGER.info("Player " + player.getName() + " connected with mods " + String.join(", ", modList));
	}
}
