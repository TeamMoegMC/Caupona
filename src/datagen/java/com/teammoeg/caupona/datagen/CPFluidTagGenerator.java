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

package com.teammoeg.caupona.datagen;

import java.nio.file.Path;
import java.util.stream.Collectors;

import com.teammoeg.caupona.Main;
import com.teammoeg.caupona.CPFluids;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CPFluidTagGenerator extends TagsProvider<Fluid> {

	public CPFluidTagGenerator(DataGenerator dataGenerator, String modId, ExistingFileHelper existingFileHelper) {
		super(dataGenerator, Registry.FLUID, modId, existingFileHelper);
	}



	@Override
	protected void addTags() {

		tag("stews").add(CPFluids.getAll().collect(Collectors.toList()).toArray(new Fluid[0]));
		tag(new ResourceLocation("frostedheart","drink")).addTag(otag("stews"));
		tag(new ResourceLocation("frostedheart","hot_drink")).addTag(otag("stews"));
		tag(new ResourceLocation("frostedheart","hidden_drink")).addTag(otag("stews"));
		tag(new ResourceLocation("watersource","drink")).add(ForgeRegistries.FLUIDS.getValue(mrl("nail_soup")));
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
		return new ResourceLocation(Main.MODID, s);
	}

	private ResourceLocation frl(String s) {
		return new ResourceLocation("forge", s);
	}


	private ResourceLocation mcrl(String s) {
		return new ResourceLocation(s);
	}

	@Override
	public String getName() {
		return Main.MODID + " fluid tags";
	}

	@Override
	protected Path getPath(ResourceLocation id) {
		return this.generator.getOutputFolder()
				.resolve("data/" + id.getNamespace() + "/tags/fluids/" + id.getPath() + ".json");
	}
}
