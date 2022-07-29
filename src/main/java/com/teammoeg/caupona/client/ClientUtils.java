package com.teammoeg.caupona.client;

import com.teammoeg.caupona.util.INetworkContainer;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class ClientUtils {

	public ClientUtils() {
	}
	public static void syncContainerInfo(CompoundTag nbt) {
		Player p=Minecraft.getInstance().player;
		if(p!=null&&p.containerMenu instanceof INetworkContainer) {
			((INetworkContainer) p.containerMenu).handle(nbt);
		}
	}
}
