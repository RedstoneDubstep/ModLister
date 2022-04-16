package redstonedubstep.mods.modlister.mixin;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.login.ServerLoginNetHandler;
import net.minecraftforge.fml.network.FMLHandshakeHandler;
import net.minecraftforge.fml.network.FMLHandshakeMessages.C2SModListReply;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import redstonedubstep.mods.modlister.ModLister;

@Mixin(FMLHandshakeHandler.class)
public abstract class MixinFMLHandshakeHandler {
	@Inject(method = "handleClientModListOnServer", at = @At("TAIL"), remap = false)
	private void onHandleClientModList(C2SModListReply clientModList, Supplier<Context> contextSupplier, CallbackInfo callback) {
		try {
			ServerLoginNetHandler packetListener = (ServerLoginNetHandler)contextSupplier.get().getNetworkManager().getPacketListener();
			GameProfile profile = packetListener.gameProfile;

			if (profile != null) {
				if (!profile.isComplete()) {
					profile = new GameProfile(PlayerEntity.createPlayerUUID(profile.getName()), profile.getName());
				}

				ModLister.updateModList(clientModList.getModList(), profile);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
