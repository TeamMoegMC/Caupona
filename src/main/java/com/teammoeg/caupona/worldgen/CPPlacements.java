package com.teammoeg.caupona.worldgen;

import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.Main;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class CPPlacements {
	public static final Holder<PlacedFeature> WALNUT_CHECKED = PlacementUtils.register(Main.MODID+":walnut_checked",CPFeatures.WALNUT, PlacementUtils.filteredByBlockSurvival(CPBlocks.WALNUT_SAPLINGS));
	public static final Holder<PlacedFeature> TREES_WALNUT = PlacementUtils.register(Main.MODID+":trees_walnut",CPFeatures.WALNUT, VegetationPlacements.treePlacement(PlacementUtils.countExtra(3, 0.1F, 1), CPBlocks.WALNUT_SAPLINGS));
	public CPPlacements() {
	}
	public static void init() {}
}
