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

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

public class PacketHandler {

	public static void send(PacketDistributor.PacketTarget target, CustomPacketPayload message) {
		target.send(message);
	}

	public static void sendToServer(CustomPacketPayload message) {
		PacketDistributor.SERVER.noArg().send(message);
	}
	public static void registerPackets(RegisterPayloadHandlerEvent ev) {
		
		IPayloadRegistrar Ch=ev.registrar(CPMain.MODID);
		Ch.play(ClientDataMessage.path, ClientDataMessage::new,ClientDataMessage::handle);
		Ch.play(ContainerDataMessage.path, ContainerDataMessage::new,ContainerDataMessage::handle);
		Ch.versioned("1");
	}
}