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
 * You should have received a copy of the GNU General Public License
 * along with Caupona. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.caupona.client;

import com.teammoeg.caupona.util.INetworkContainer;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;

public class ClientUtils {

	public ClientUtils() {
	}

	public static void syncContainerInfo(CompoundTag nbt) {
		Player p = Minecraft.getInstance().player;
		if (p != null && p.containerMenu instanceof INetworkContainer) {
			((INetworkContainer) p.containerMenu).handle(nbt);
		}
	}

}
