package com.teammoeg.caupona.blocks;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

public class CPHorizontalTileBlock<V extends BlockEntity> extends CPHorizontalBlock implements CPTileBlock<V> {
	private final RegistryObject<BlockEntityType<V>> te;


	public CPHorizontalTileBlock(RegistryObject<BlockEntityType<V>> te,Properties p_54120_) {
		super(p_54120_);
		this.te = te;
	}


	@Override
	public RegistryObject<BlockEntityType<V>> getTile() {
		return te;
	}

}
