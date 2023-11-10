package redstonedubstep.mods.modlistobserver;

import org.apache.commons.lang3.tuple.Pair;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

public class ModListObserverConfig {
	public static final ModConfigSpec SERVER_SPEC;
	public static final Config CONFIG;

	static {
		final Pair<Config, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Config::new);

		SERVER_SPEC = specPair.getRight();
		CONFIG = specPair.getLeft();
	}

	public static class Config {
		public BooleanValue logJoiningModList;
		public BooleanValue logServerMods;
		public IntValue modlistCommandPermissionLevel;

		Config(ModConfigSpec.Builder builder) {
			logJoiningModList = builder
					.comment(" --- ModListObserver Config File --- ", "Should the current mod list of joining players be logged?")
					.define("logJoiningModList", true);
			logServerMods = builder
					.comment("Should mods of a joining player that are also present on the server be logged?")
					.define("logServerMods", true);
			modlistCommandPermissionLevel = builder
					.comment("What op permission level should be the requirement for being able to execute /modlist?")
					.defineInRange("modlistCommandPermissionLevel", 3, 0, 4);
		}
	}
}