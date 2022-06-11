package redstonedubstep.mods.modlistobserver.mixin;

import java.util.HashSet;
import java.util.List;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.network.HandshakeHandler;
import net.minecraftforge.network.HandshakeMessages.C2SModListReply;
import net.minecraftforge.network.NetworkEvent.Context;
import redstonedubstep.mods.modlistobserver.ModListObserver;
import redstonedubstep.mods.modlistobserver.ModListObserverConfig;

@Mixin(HandshakeHandler.class)
public abstract class MixinHandshakeHandler {
	@Inject(method = "handleClientModListOnServer", at = @At("TAIL"), remap = false)
	private void onHandleClientModList(C2SModListReply clientModList, Supplier<Context> contextSupplier, CallbackInfo callback) {
		ServerLoginPacketListenerImpl packetListener = (ServerLoginPacketListenerImpl)contextSupplier.get().getNetworkManager().getPacketListener();
		GameProfile profile = packetListener.gameProfile;
		List<String> serverMods = ModList.get().getMods().stream().map(IModInfo::getModId).toList();
		List<String> clientMods = clientModList.getModList();

		if (!ModListObserverConfig.CONFIG.logServerMods.get())
			clientMods = clientMods.stream().filter(s -> !serverMods.contains(s)).toList();

		if (profile != null) {
			if (!profile.isComplete())
				profile = new GameProfile(Player.createPlayerUUID(profile), profile.getName());

			ModListObserver.updateModListOnJoin(new HashSet<>(clientMods), profile);
		}
	}
}
