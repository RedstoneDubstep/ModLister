package redstonedubstep.mods.modlister;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

@Mod(ModLister.MODID)
public class ModLister {
	public static final String MODID = "modlister";
	private static final Map<GameProfile, List<String>> ALL_MODS = new HashMap<>();
	private static final Map<GameProfile, List<String>> CURRENT_MODS = new HashMap<>();
	private static final Logger LOGGER = LogManager.getLogger();

	public ModLister() {
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
		MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
	}

	public void registerCommands(RegisterCommandsEvent event) {
		ModListCommand.register(event.getDispatcher());
	}

	public static List<String> getAllMods(GameProfile player) {
		return ALL_MODS.get(player);
	}

	public static List<String> getCurrentMods(GameProfile player) {
		return CURRENT_MODS.get(player);
	}

	public static void updateModList(List<String> modList, GameProfile player) {
		CURRENT_MODS.put(player, modList);

		if (ALL_MODS.containsKey(player))
			ALL_MODS.get(player).addAll(modList.stream().filter(s -> !ALL_MODS.get(player).contains(s)).collect(Collectors.toList()));
		else
			ALL_MODS.put(player, modList);

		LOGGER.info("Player " + player.getName() + " connected with mods " + String.join(", ", modList));
	}
}
