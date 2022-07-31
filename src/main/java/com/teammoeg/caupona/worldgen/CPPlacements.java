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

package com.teammoeg.caupona.worldgen;

import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.Main;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class CPPlacements {
	public static final Holder<PlacedFeature> TREES_WALNUT = PlacementUtils.register(Main.MODID + ":trees_walnut",
			CPFeatures.WALNUT,
			VegetationPlacements.treePlacement(PlacementUtils.countExtra(0, 0.125F, 1), CPBlocks.WALNUT_SAPLINGS));
	public static final Holder<PlacedFeature> TREES_FIG = PlacementUtils.register(Main.MODID + ":trees_fig",
			CPFeatures.FIG,
			VegetationPlacements.treePlacement(PlacementUtils.countExtra(0, 0.125F, 1), CPBlocks.FIG_SAPLINGS));
	public static final Holder<PlacedFeature> TREES_WOLFBERRY = PlacementUtils.register(Main.MODID + ":trees_wolfberry",
			CPFeatures.WOLFBERRY,
			VegetationPlacements.treePlacement(PlacementUtils.countExtra(0, 0.125F, 1), CPBlocks.WOLFBERRY_SAPLINGS));

	public CPPlacements() {
	}

	public static void init() {
	}
}
