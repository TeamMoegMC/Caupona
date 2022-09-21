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

package com.teammoeg.caupona.datagen;

import com.teammoeg.caupona.Main;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CPDataGenerator {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator gen = event.getGenerator();
		ExistingFileHelper exHelper = event.getExistingFileHelper();

		if (event.includeServer()) {
			gen.addProvider(new CPItemModelProvider(gen, Main.MODID, exHelper));
			gen.addProvider(new CPRecipeProvider(gen));
			gen.addProvider(new CPItemTagGenerator(gen, Main.MODID, exHelper));
			gen.addProvider(new CPBlockTagGenerator(gen, Main.MODID, exHelper));
			gen.addProvider(new CPFluidTagGenerator(gen, Main.MODID, exHelper));
			gen.addProvider(new CPLootGenerator(gen));
			gen.addProvider(new CPStatesProvider(gen, Main.MODID, exHelper));
			gen.addProvider(new CPBookGenerator(gen, exHelper));
		}
	}
}
