/*
 * Copyright (c) 2024 TeamMoeg
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
 * Specially, we allow this software to be used alongside with closed source software Minecraft(R) and Forge or other modloader.
 * Any mods or plugins can also use apis provided by forge or com.teammoeg.caupona.api without using GPL or open source.
 *
 * You should have received a copy of the GNU General Public License
 * along with Caupona. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.caupona.network;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.BlockCapability;

public abstract class CPBaseBlockEntity extends BlockEntity {

	public CPBaseBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
		super(pType, pWorldPosition, pBlockState);
	}


	public void syncData() {
		if (this.level != null) {
			this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
		}
		this.setChanged();
	}

	public abstract void readCustomNBT(ValueInput nbt, boolean isClient);

	public abstract void writeCustomNBT(ValueOutput nbt, boolean isClient);

	private boolean fromNetwork = false;

	@Override
	public void onDataPacket(Connection net, ValueInput valueInput) {
		try {
			fromNetwork = true;
			super.onDataPacket(net, valueInput);
		} finally {
			fromNetwork = false;
		}
	}

	public abstract void tick();

	public Object getCapability(BlockCapability<?, Direction> type, Direction d) {
		return null;
	};

	@Override
	public void loadAdditional(ValueInput valueInput) {
		this.readCustomNBT(valueInput, fromNetwork);
		super.loadAdditional(valueInput);

	}
	public abstract void handleMessage(short type, int data) ;
	@Override
	protected void saveAdditional(ValueOutput valueOutput) {
		this.writeCustomNBT(valueOutput, false);
		super.saveAdditional(valueOutput);
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
	private static final Logger LOGGER=LogUtils.getLogger();
	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(this.problemPath(), LOGGER)) {
			TagValueOutput tvo = TagValueOutput.createWithContext(reporter, registries);
			writeCustomNBT(tvo, true);
			return tvo.buildResult();
		}
	}

	public boolean isHandlingPacket() {
		return fromNetwork;
	}
}
