package redstonedubstep.mods.modlistobserver;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;

public class ModListObserverConfig {
	public static final ForgeConfigSpec SERVER_SPEC;
	public static final Config CONFIG;

	static {
		final Pair<Config, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Config::new);

		SERVER_SPEC = specPair.getRight();
		CONFIG = specPair.getLeft();
	}

	public static class Config {
		public BooleanValue logJoiningModList;

		Config(ForgeConfigSpec.Builder builder) {
			logJoiningModList = builder
					.comment(" --- ModListObserver Config File --- ", "Should the current mod list of joining players be logged?")
					.define("logJoiningModList", true);
		}
	}
}