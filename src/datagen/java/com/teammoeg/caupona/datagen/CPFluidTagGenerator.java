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


import java.util.concurrent.CompletableFuture;

import com.teammoeg.caupona.CPFluids;
import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.CPTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CPFluidTagGenerator extends TagsProvider<Fluid> {

	public CPFluidTagGenerator(DataGenerator dataGenerator, String modId, ExistingFileHelper existingFileHelper,CompletableFuture<HolderLookup.Provider> provider) {
		super(dataGenerator.getPackOutput(), Registries.FLUID, provider,modId,existingFileHelper);
	}

	@Override
	protected void addTags(Provider p) {
		TagAppender<Fluid> stews=tag(CPTags.Fluids.STEWS);
		TagAppender<Fluid> boilable=tag(CPTags.Fluids.BOILABLE).add(ForgeRegistries.FLUIDS.getResourceKey(Fluids.WATER).get()).add(ForgeMod.MILK.getKey())
				.addTag(CPTags.Fluids.STEWS);
		CPFluids.getAllKeys().forEach(stews::add);
		tag(CPTags.Fluids.ANY_WATER).add(ResourceKey.create(Registries.FLUID,mrl("stock"))).add(ResourceKey.create(Registries.FLUID,mrl("nail_soup")));
		
		tag(CPTags.Fluids.PUMICE_ON).add(ForgeRegistries.FLUIDS.getResourceKey(Fluids.WATER).get());
		tag(new ResourceLocation("watersource", "drink")).add(ResourceKey.create(Registries.FLUID,mrl("nail_soup")));
	}
	private Fluid cp(String s) {
		Fluid i = ForgeRegistries.FLUIDS.getValue(mrl(s));
		return i;// just going to cause trouble if not exists
	}
	private TagAppender<Fluid> tag(String s) {
		return this.tag(FluidTags.create(mrl(s)));
	}

	private TagAppender<Fluid> tag(ResourceLocation s) {
		return this.tag(FluidTags.create(s));
	}

	private ResourceLocation rl(RegistryObject<Fluid> it) {
		return it.getId();
	}

	private ResourceLocation rl(String r) {
		return new ResourceLocation(r);
	}

	private TagKey<Fluid> otag(String s) {
		return FluidTags.create(mrl(s));
	}

	private TagKey<Fluid> atag(ResourceLocation s) {
		return FluidTags.create(s);
	}

	private ResourceLocation mrl(String s) {
		return new ResourceLocation(CPMain.MODID, s);
	}

	private ResourceLocation frl(String s) {
		return new ResourceLocation("forge", s);
	}

	private ResourceLocation mcrl(String s) {
		return new ResourceLocation(s);
	}

	@Override
	public String getName() {
		return CPMain.MODID + " fluid tags";
	}
/*
	@Override
	protected Path getPath(ResourceLocation id) {
		return this.generator.getOutputFolder()
				.resolve("data/" + id.getNamespace() + "/tags/fluids/" + id.getPath() + ".json");
	}*/

}
