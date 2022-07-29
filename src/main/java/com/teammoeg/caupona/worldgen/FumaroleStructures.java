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


    public static Optional<PieceGenerator<JigsawConfiguration>> createPiecesGenerator(PieceGeneratorSupplier.Context<JigsawConfiguration> context) {


        return Optional.of((builder,ctx)->{
        		BlockPos blockpos=ctx.chunkPos().getBlockAt(ctx.random().nextInt(16), 0,ctx.random().nextInt(16));
        		int topLandY = context.chunkGenerator().getFirstFreeHeight(blockpos.getX(), blockpos.getZ(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor());
        		blockpos=blockpos.atY(topLandY-4+ctx.random().nextInt(1));
                JigsawPlacement.addPieces(
                        context,
                        PoolElementStructurePiece::new,
                        blockpos,
                        false, 
                        false 
                ).ifPresent(t->t.generatePieces(builder, ctx));
        		});
    }
}