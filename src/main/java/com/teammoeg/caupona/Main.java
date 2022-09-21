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

package com.teammoeg.caupona;

import javax.annotation.Nonnull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.teammoeg.caupona.client.Particles;
import com.teammoeg.caupona.data.RecipeReloadListener;
import com.teammoeg.caupona.network.PacketHandler;
import com.teammoeg.caupona.worldgen.CPStructures;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Main.MODID)
public class Main {

	public static final String MODID = "caupona";
	public static final String MODNAME = "Caupona";
	public static final Logger logger = LogManager.getLogger(MODNAME);
	public static final String BOOK_NBT_TAG=Main.MODID+":book_given";
	public static final CreativeModeTab mainGroup = new CreativeModeTab(MODID) {
		@Override
		@Nonnull
		public ItemStack makeIcon() {
			return new ItemStack(CPBlocks.stew_pot);
		}

	};
	public static final CreativeModeTab foodGroup = new CreativeModeTab(MODID + "_foods") {
		@Override
		@Nonnull
		public ItemStack makeIcon() {
			return new ItemStack(CPItems.gravy_boat);
		}

	};

	public static ResourceLocation rl(String path) {
		return new ResourceLocation(MODID, path);
	}

	public Main() {
		IEventBus mod = FMLJavaModLoadingContext.get().getModEventBus();
		ForgeMod.enableMilkFluid();

		CPFluids.init();
		CPTileTypes.REGISTER.register(mod);
		CPGui.CONTAINERS.register(mod);
		Particles.REGISTER.register(mod);
		MinecraftForge.EVENT_BUS.register(RecipeReloadListener.class);
		CPFluids.FLUIDS.register(mod);
		CPRecipes.RECIPE_SERIALIZERS.register(mod);
		CPEntityTypes.ENTITY_TYPES.register(mod);
		CPStructures.STRUCTURES.register(mod);
		Config.register();
		PacketHandler.register();
	}

}
