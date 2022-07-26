package com.teammoeg.caupona.worldgen;

import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.Main;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class CPPlacements {
	public static final Holder<PlacedFeature> TREES_WALNUT = PlacementUtils.register(Main.MODID+":trees_walnut",CPFeatures.WALNUT, VegetationPlacements.treePlacement(PlacementUtils.countExtra(0, 0.125F, 1), CPBlocks.WALNUT_SAPLINGS));
	public static final Holder<PlacedFeature> TREES_FIG = PlacementUtils.register(Main.MODID+":trees_fig",CPFeatures.FIG, VegetationPlacements.treePlacement(PlacementUtils.countExtra(0, 0.125F, 1), CPBlocks.FIG_SAPLINGS));
	public static final Holder<PlacedFeature> TREES_WOLFBERRY = PlacementUtils.register(Main.MODID+":trees_wolfberry",CPFeatures.WOLFBERRY, VegetationPlacements.treePlacement(PlacementUtils.countExtra(0, 0.125F, 1), CPBlocks.WOLFBERRY_SAPLINGS));
	
	public CPPlacements() {
	}
	public static void init() {}
}
