package com.teammoeg.caupona.worldgen;

import java.util.HashSet;
import java.util.Set;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.CPWorldGen;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

public class LeavingLogReplacer extends TreeDecorator {
	public static final Codec<LeavingLogReplacer> CODEC = RecordCodecBuilder.create(t ->
	t.group(BlockStateProvider.CODEC.fieldOf("replace").forGetter(o->o.state)).apply(t, LeavingLogReplacer::new)
		);
	BlockStateProvider state;
	public LeavingLogReplacer(BlockStateProvider state) {
		super();
		this.state = state;
	}

	@Override
	protected TreeDecoratorType<?> type() {
		return CPWorldGen.REPLACE_LOG.get();
	}

	@Override
	public void place(Context pContext) {
		Set<BlockPos> leaves=new HashSet<>(pContext.leaves());
		for(BlockPos bp:pContext.logs()){
			boolean n=leaves.contains(bp.north());
			boolean s=leaves.contains(bp.south());
			boolean e=leaves.contains(bp.east());
			boolean w=leaves.contains(bp.west());
			//System.out.println("n:"+n+"s:"+s+"e:"+e+"w:"+w);
			if(n||s||w||e) {
				pContext.setBlock(bp, state.getState(pContext.random(), bp));
			}
		}
	}

}
