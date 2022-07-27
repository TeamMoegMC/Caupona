package com.teammoeg.caupona.util;

import com.teammoeg.caupona.network.ContainerDataMessage;
import com.teammoeg.caupona.network.PacketHandler;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

public interface INetworkContainer {
	void handle(CompoundTag nbt);
	ServerPlayer getOpenedPlayer();
	default void sendMessage(CompoundTag nbt) {
		PacketHandler.send(PacketDistributor.PLAYER.with(this::getOpenedPlayer),new ContainerDataMessage(nbt));
	}
}
