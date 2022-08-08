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

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.PostPlacementProcessor;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.Heightmap;

public class FumaroleStructures extends StructureFeature<JigsawConfiguration> {

	public FumaroleStructures() {
		super(JigsawConfiguration.CODEC, FumaroleStructures::createPiecesGenerator, PostPlacementProcessor.NONE);
	}

	@Override
	public GenerationStep.Decoration step() {
		return GenerationStep.Decoration.SURFACE_STRUCTURES;
	}

	public static Optional<PieceGenerator<JigsawConfiguration>> createPiecesGenerator(
			PieceGeneratorSupplier.Context<JigsawConfiguration> context) {

		return Optional.of((builder, ctx) -> {
			BlockPos blockpos = ctx.chunkPos().getBlockAt(ctx.random().nextInt(16), 0, ctx.random().nextInt(16));
			int topLandY = context.chunkGenerator().getFirstFreeHeight(blockpos.getX(), blockpos.getZ(),
					Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor());
			blockpos = blockpos.atY(topLandY - 4 + ctx.random().nextInt(1));
			JigsawPlacement.addPieces(context, PoolElementStructurePiece::new, blockpos, false, false)
					.ifPresent(t -> t.generatePieces(builder, ctx));
		});
	}
}