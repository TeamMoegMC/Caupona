package com.teammoeg.caupona.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;

public class WorldDropOperation extends SnapshotJournal<List<ItemStack>> {
	protected List<ItemStack> toDrop=new ArrayList<>();
	protected Level level;
	protected BlockPos pos;
	public WorldDropOperation(Level level, BlockPos pos) {
		super();
		this.level = level;
		this.pos = pos;
	}
	public void addDrop(ItemStack stack) {
		toDrop.add(stack);
	}
	public void addDrops(ItemStack... stack) {
		toDrop.addAll(List.of(stack));
	}
	@Override
	protected List<ItemStack> createSnapshot() {
		// TODO Auto-generated method stub
		return new ArrayList<>(toDrop);
	}

	@Override
	protected void revertToSnapshot(List<ItemStack> snapshot) {
		toDrop.clear();
		toDrop.addAll(snapshot);
		
	}

	@Override
	protected void onRootCommit(List<ItemStack> originalState) {
		super.onRootCommit(originalState);
		for(ItemStack stack:toDrop) {
			ItemEntity entityitem = new ItemEntity(level, pos.getX(), pos.getY() + 0.5, pos.getZ(),stack);
            entityitem.setPickUpDelay(40);
            entityitem.setDeltaMovement(entityitem.getDeltaMovement().multiply(0, 1, 0));

            level.addFreshEntity(entityitem);
		}
		toDrop.clear();
	}

}
