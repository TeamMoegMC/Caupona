/*
 * Copyright (c) 2022 TeamMoeg
 *
 * This file is part of Caupona.
 *
 * Caupona is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Caupona is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * Specially, we allow this software to be used alongside with closed source software Minecraft(R) and Forge or other modloader.
 * Any mods or plugins can also use apis provided by forge or com.teammoeg.caupona.api without using GPL or open source.
 *
 * You should have received a copy of the GNU General Public License
 * along with Caupona. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.caupona.network;

import java.util.function.Supplier;

import com.teammoeg.caupona.client.ClientProxy;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public class ContainerDataMessage {
	private CompoundTag nbt;

	public ContainerDataMessage(CompoundTag message) {
		this.nbt = message;
	}

	ContainerDataMessage(FriendlyByteBuf buffer) {
		nbt = buffer.readNbt();
	}

	void encode(FriendlyByteBuf buffer) {
		buffer.writeNbt(nbt);
	}

	void handle(Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(() -> {
			ClientProxy.data = nbt;
			DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientProxy::run);
		});
		context.get().setPacketHandled(true);
	}
}
