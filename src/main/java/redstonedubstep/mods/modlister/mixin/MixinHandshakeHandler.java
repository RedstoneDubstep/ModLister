package redstonedubstep.mods.modlister.mixin;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.HandshakeHandler;
import net.minecraftforge.network.HandshakeMessages.C2SModListReply;
import net.minecraftforge.network.NetworkEvent.Context;
import redstonedubstep.mods.modlister.ModLister;

@Mixin(HandshakeHandler.class)
public abstract class MixinHandshakeHandler {
	@Inject(method = "handleClientModListOnServer", at = @At("TAIL"), remap = false)
	private void onHandleClientModList(C2SModListReply clientModList, Supplier<Context> contextSupplier, CallbackInfo callback) {
		ServerLoginPacketListenerImpl packetListener = (ServerLoginPacketListenerImpl)contextSupplier.get().getNetworkManager().getPacketListener();
		GameProfile profile = packetListener.gameProfile;

		if (profile != null) {
			if (!profile.isComplete()) {
				profile = new GameProfile(Player.createPlayerUUID(profile.getName()), profile.getName());
			}

			ModLister.updateModList(clientModList.getModList(), profile);
		}
	}
}
