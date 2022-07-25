package com.teammoeg.caupona.blocks;

import com.teammoeg.caupona.network.CPBaseTile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;

public interface CPTileBlock<V extends BlockEntity> extends EntityBlock{
	@Override
	public default BlockEntity newBlockEntity(BlockPos p, BlockState s) {
		return getTile().get().create(p, s);
	}
	RegistryObject<BlockEntityType<V>> getTile();
	@Override
	public default <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState,
			BlockEntityType<T> pBlockEntityType) {
		return new BlockEntityTicker<T>() {

			@Override
			public void tick(Level pLevel, BlockPos pPos, BlockState pState, BlockEntity pBlockEntity) {
				if (!pBlockEntity.hasLevel())
					pBlockEntity.setLevel(pLevel);
				if(pBlockEntity instanceof CPBaseTile)
					((CPBaseTile) pBlockEntity).tick();
			}
		};
	}
}
