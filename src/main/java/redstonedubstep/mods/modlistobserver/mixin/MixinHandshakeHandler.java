package redstonedubstep.mods.modlistobserver.mixin;

import java.util.HashSet;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.HandshakeHandler;
import net.neoforged.neoforge.network.HandshakeMessages;
import net.neoforged.neoforge.network.NetworkEvent;
import net.neoforged.neoforgespi.language.IModInfo;
import redstonedubstep.mods.modlistobserver.ModListObserver;
import redstonedubstep.mods.modlistobserver.ModListObserverConfig;

@Mixin(HandshakeHandler.class)
public abstract class MixinHandshakeHandler {
	@Inject(method = "handleClientModListOnServer", at = @At("TAIL"), remap = false)
	private void onHandleClientModList(HandshakeMessages.C2SModListReply clientModList, NetworkEvent.Context context, CallbackInfo callback) {
		ServerLoginPacketListenerImpl packetListener = (ServerLoginPacketListenerImpl)context.getNetworkManager().getPacketListener();
		GameProfile profile = packetListener.authenticatedProfile;
		List<String> serverMods = ModList.get().getMods().stream().map(IModInfo::getModId).toList();
		List<String> clientMods = clientModList.getModList();

		if (!ModListObserverConfig.CONFIG.logServerMods.get())
			clientMods = clientMods.stream().filter(s -> !serverMods.contains(s)).toList();

		if (profile != null)
			ModListObserver.updateModListOnJoin(new HashSet<>(clientMods), profile);
	}
}
