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

import java.util.function.Consumer;

import javax.annotation.Nonnull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.teammoeg.caupona.client.Particles;
import com.teammoeg.caupona.compat.treechop.TreechopCompat;
import com.teammoeg.caupona.data.RecipeReloadListener;
import com.teammoeg.caupona.network.PacketHandler;
import com.teammoeg.caupona.worldgen.CPFeatures;
import com.teammoeg.caupona.worldgen.CPPlacements;
import com.teammoeg.caupona.worldgen.CPStructures;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CPMain.MODID)
public class CPMain {

	public static final String MODID = "caupona";
	public static final String MODNAME = "Caupona";
	public static final Logger logger = LogManager.getLogger(MODNAME);
	public static final String BOOK_NBT_TAG=CPMain.MODID+":book_given";
	public static final CreativeModeTab mainGroup = new CreativeModeTab(MODID) {
		@Override
		@Nonnull
		public ItemStack makeIcon() {
			return new ItemStack(CPBlocks.stew_pot.get());
		}

	};
	public static final CreativeModeTab foodGroup = new CreativeModeTab(MODID + "_foods") {
		@Override
		@Nonnull
		public ItemStack makeIcon() {
			return new ItemStack(CPItems.gravy_boat.get());
		}

	};

	public static ResourceLocation rl(String path) {
		return new ResourceLocation(MODID, path);
	}

	public CPMain() {
		IEventBus mod = FMLJavaModLoadingContext.get().getModEventBus();
		ForgeMod.enableMilkFluid();
		MinecraftForge.EVENT_BUS.register(RecipeReloadListener.class);
		mod.addListener(this::enqueueIMC);
		CPBlockEntityTypes.REGISTER.register(mod);
		CPGui.CONTAINERS.register(mod);
		Particles.REGISTER.register(mod);
		CPFluids.FLUIDS.register(mod);
		CPFluids.FLUID_TYPES.register(mod);
		CPBlocks.BLOCKS.register(mod);
		CPItems.ITEMS.register(mod);
		CPFeatures.FEATURES.register(mod);
		CPPlacements.PLACEMENTS.register(mod);
		CPRecipes.RECIPE_SERIALIZERS.register(mod);
		CPEntityTypes.ENTITY_TYPES.register(mod);
		CPRecipes.RECIPE_TYPES.register(mod);
		CPStructures.TYPES.register(mod);
		CPFeatures.FOILAGE_TYPES.register(mod);
		CPFeatures.TRUNK_TYPES.register(mod);
		Config.register();
		PacketHandler.register();
		
	}

	public void enqueueIMC(InterModEnqueueEvent event) {
	    InterModComms.sendTo("treechop", "getTreeChopAPI", () -> (Consumer)TreechopCompat::new);
	}
}
