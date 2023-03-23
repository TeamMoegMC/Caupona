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

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
	private static final String VERSION = Integer.toString(1);
	private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(CPMain.MODID, "network"), () -> VERSION, VERSION::equals, VERSION::equals);

	public static void send(PacketDistributor.PacketTarget target, Object message) {
		CHANNEL.send(target, message);
	}

	public static void sendToServer(Object message) {
		CHANNEL.sendToServer(message);
	}

	public static SimpleChannel get() {
		return CHANNEL;
	}

	public static void register() {
		int id = 0;
		CHANNEL.registerMessage(id++, ClientDataMessage.class, ClientDataMessage::encode, ClientDataMessage::new,
				ClientDataMessage::handle);
		CHANNEL.registerMessage(id++, ContainerDataMessage.class, ContainerDataMessage::encode,
				ContainerDataMessage::new, ContainerDataMessage::handle);
	}
}