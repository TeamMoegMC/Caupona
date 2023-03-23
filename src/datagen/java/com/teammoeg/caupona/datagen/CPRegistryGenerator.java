package com.teammoeg.caupona.datagen;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPWorldGen;
import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.worldgen.BushFoliagePlacer;
import com.teammoeg.caupona.worldgen.BushStraightTrunkPlacer;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CPRegistryGenerator extends DatapackBuiltinEntriesProvider {

	@SuppressWarnings("unchecked")
	public CPRegistryGenerator(PackOutput output, CompletableFuture<Provider> registries) {
		super(output, registries,new RegistrySetBuilder()
				
				.add(Registries.CONFIGURED_FEATURE,(RegistrySetBuilder.RegistryBootstrap)CPRegistryGenerator::bootstrapCFeatures)
				.add(Registries.PLACED_FEATURE,CPRegistryGenerator::bootstrapPFeatures),
				Set.of(CPMain.MODID));
		
	}
	
	
	public static void bootstrapPFeatures(BootstapContext<PlacedFeature> pContext) {
		HolderGetter<ConfiguredFeature<?, ?>> holder=pContext.lookup(Registries.CONFIGURED_FEATURE);
		PlacementUtils.register(pContext, CPWorldGen.TREES_WALNUT,holder.getOrThrow(CPWorldGen.WALNUT),VegetationPlacements
				.treePlacement(PlacementUtils.countExtra(0, 0.125F, 1), sap("walnut")));
		PlacementUtils.register(pContext, CPWorldGen.TREES_FIG,holder.getOrThrow(CPWorldGen.FIG),VegetationPlacements
				.treePlacement(PlacementUtils.countExtra(0, 0.125F, 1), sap("fig")));
		PlacementUtils.register(pContext, CPWorldGen.TREES_WOLFBERRY,holder.getOrThrow(CPWorldGen.WOLFBERRY),VegetationPlacements
				.treePlacement(PlacementUtils.countExtra(0, 0.125F, 1), sap("wolfberry")));

	}
	public static void bootstrapCFeatures(BootstapContext<ConfiguredFeature<?,?>> pContext) {
		FeatureUtils.register(pContext,CPWorldGen.WALNUT,Feature.TREE,createStraightBlobTree(log("walnut"),leave("walnut"), 4, 2, 0, 2).ignoreVines().build());
		FeatureUtils.register(pContext,CPWorldGen.FIG,Feature.TREE,createStraightBlobBush(log("fig"), leave("fig"), 4, 2, 0, 2).ignoreVines().build());
		FeatureUtils.register(pContext,CPWorldGen.WOLFBERRY,Feature.TREE,createStraightBlobBush(log("wolfberry"),leave("wolfberry"), 4, 2, 0, 2).ignoreVines().build());
	}
	private static TreeConfiguration.TreeConfigurationBuilder createStraightBlobTree(Block log, Block leave, int height,
			int randA, int randB, int foliage) {
		return new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(log),
				new StraightTrunkPlacer(height, randA, randB), BlockStateProvider.simple(leave),
				new BlobFoliagePlacer(ConstantInt.of(foliage), ConstantInt.of(0), 3),
				new TwoLayersFeatureSize(1, 0, 1));
	}

	private static TreeConfiguration.TreeConfigurationBuilder createStraightBlobBush(Block log, Block leave, int height,
			int randA, int randB, int foliage) {
		return new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(log),
				new BushStraightTrunkPlacer(height, randA, randB), BlockStateProvider.simple(leave),
				new BushFoliagePlacer(ConstantInt.of(foliage), ConstantInt.of(0), 3),
				new TwoLayersFeatureSize(1, 0, 1));
	}
	public static Block leave(String type) {
		return block(type+"_leaves");
	}
	public static Block sap(String type) {
		return block(type+"_sapling");
	}
	public static Block log(String type) {
		return block(type+"_log");
	}
	public static Block block(String type) {
		return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(CPMain.MODID,type));
	}
	@Override
	public String getName() {
		return "Caupona Registry Generator";
	}
}
