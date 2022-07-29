package com.teammoeg.caupona.client;

import net.minecraft.nbt.CompoundTag;

public class ClientProxy {
	public static CompoundTag data;
	public ClientProxy() {
	}
	public static void run() {
		ClientUtils.syncContainerInfo(data);
	}
}
