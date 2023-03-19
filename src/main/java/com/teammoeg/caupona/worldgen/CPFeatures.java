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

package com.teammoeg.caupona.worldgen;

import com.teammoeg.caupona.Main;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CPFeatures {
	public static final DeferredRegister<FoliagePlacerType<?>> FOILAGE_TYPES = DeferredRegister.create(Registries.FOLIAGE_PLACER_TYPE, Main.MODID);
	public static final DeferredRegister<TrunkPlacerType<?>> TRUNK_TYPES = DeferredRegister.create(Registries.TRUNK_PLACER_TYPE, Main.MODID);
	
	
	public static final ResourceKey<ConfiguredFeature<?, ?>> WALNUT = FeatureUtils.createKey("caupona:walnut");
	public static final ResourceKey<ConfiguredFeature<?, ?>> FIG = FeatureUtils.createKey("caupona:fig");
	public static final ResourceKey<ConfiguredFeature<?, ?>> WOLFBERRY = FeatureUtils.createKey("caupona:wolfberry");
	
	public static final RegistryObject<FoliagePlacerType<BushFoliagePlacer>> BUSH_PLACER = FOILAGE_TYPES
			.register("bush_foliage_placer", () -> new FoliagePlacerType<>(BushFoliagePlacer.CODEC));
	
	public static final RegistryObject<TrunkPlacerType<BushStraightTrunkPlacer>> BUSH_TRUNK = TRUNK_TYPES
			.register("bush_chunk", () -> new TrunkPlacerType<>(BushStraightTrunkPlacer.CODEC));

	public CPFeatures() {
	}




}
