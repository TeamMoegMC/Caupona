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

import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.client.ClientProxy;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class ContainerDataMessage implements CustomPacketPayload{
	private CompoundTag nbt;
	public static final ResourceLocation path=new ResourceLocation(CPMain.MODID,"container_data");
	public ContainerDataMessage(CompoundTag message) {
		this.nbt = message;
	}

	ContainerDataMessage(FriendlyByteBuf buffer) {
		nbt = buffer.readNbt();
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeNbt(nbt);
	}

	void handle(PlayPayloadContext context) {
		{
			ClientProxy.data = nbt;
			if(FMLEnvironment.dist==Dist.CLIENT)
				ClientProxy.run();
		}
	}

	@Override
	public ResourceLocation id() {
		return path;
	}

}
