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

import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.Main;

import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CPPlacements {
	public static final DeferredRegister<PlacedFeature> PLACEMENTS = DeferredRegister
			.create(Registry.PLACED_FEATURE_REGISTRY, Main.MODID);
	public static final RegistryObject<PlacedFeature> TREES_WALNUT = PLACEMENTS.register("trees_walnut",
			() -> new PlacedFeature(CPFeatures.WALNUT.getHolder().get(), VegetationPlacements
					.treePlacement(PlacementUtils.countExtra(0, 0.125F, 1), CPBlocks.WALNUT_SAPLINGS)));
	public static final RegistryObject<PlacedFeature> TREES_FIG = PLACEMENTS.register("trees_fig",
			() -> new PlacedFeature(CPFeatures.FIG.getHolder().get(), VegetationPlacements
					.treePlacement(PlacementUtils.countExtra(0, 0.125F, 1), CPBlocks.FIG_SAPLINGS)));
	public static final RegistryObject<PlacedFeature> TREES_WOLFBERRY = PLACEMENTS.register("trees_wolfberry",
			() -> new PlacedFeature(CPFeatures.WOLFBERRY.getHolder().get(), VegetationPlacements
					.treePlacement(PlacementUtils.countExtra(0, 0.125F, 1), CPBlocks.WOLFBERRY_SAPLINGS)));

	public CPPlacements() {
	}

}
