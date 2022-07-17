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

package com.teammoeg.caupona.blocks;

import java.util.Random;
import java.util.function.BiFunction;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.RegistryObject;

public class KitchenStove extends CPBaseBlock<KitchenStoveTileEntity> {
	
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty LIT = BlockStateProperties.LIT;
	public static final BooleanProperty ASH = BooleanProperty.create("ash");
	public static final IntegerProperty FUELED = IntegerProperty.create("fueled", 0, 2);


	public KitchenStove(String name,Properties blockProps,RegistryObject<BlockEntityType<KitchenStoveTileEntity>> ste,
			BiFunction<Block, Item.Properties, Item> createItemBlock) {
		super(name, blockProps, ste, createItemBlock);
	}
	/*@Override
	public VoxelShape getVisualShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}*/
	@Override
	@OnlyIn(Dist.CLIENT)
	public float getShadeBrightness(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 1.0F;
	}
	@Override
	public boolean useShapeForLightOcclusion(BlockState state) {
		return true;
	}
	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player,
			InteractionHand handIn, BlockHitResult hit) {
		InteractionResult p = super.use(state, worldIn, pos, player, handIn, hit);
		if (p.consumesAction())
			return p;
		KitchenStoveTileEntity tileEntity = (KitchenStoveTileEntity) worldIn.getBlockEntity(pos);
		/*for(Item i:ForgeRegistries.ITEMS) {
			if(CountingTags.tags.stream().anyMatch(i.getTags()::contains)&&!i.isFood()&&FoodValueRecipe.recipes.get(i)==null)
				System.out.println(i.getRegistryName());
		}*/
		if (handIn == InteractionHand.MAIN_HAND) {
			if (tileEntity != null && !worldIn.isClientSide)
				NetworkHooks.openGui((ServerPlayer) player, tileEntity, tileEntity.getBlockPos());
			return InteractionResult.SUCCESS;
		}
		return p;
	}

	@Override
	public void animateTick(BlockState stateIn, Level worldIn, BlockPos bp, Random rand) {
		BlockEntity te = worldIn.getBlockEntity(bp);
		if (stateIn.getValue(LIT)) {
			double d0 = bp.getX();
			double d1 = bp.getY();
			double d2 = bp.getZ();
			if (rand.nextDouble() < 0.2D) {
				worldIn.playLocalSound(d0, d1, d2, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F,
						false);
			}
			worldIn.addParticle(ParticleTypes.FLAME, d0 + rand.nextDouble(), bp.getY() + 1, d2 + rand.nextDouble(),
					0.0D, 0.0D, 0.0D);
			if (rand.nextDouble() < 0.5D) {
				worldIn.addParticle(ParticleTypes.SMOKE, d0 + .5, bp.getY() + 1, d2 + .5, rand.nextDouble() * .5 - .25,
						rand.nextDouble() * .125, rand.nextDouble() * .5 - .25);

			}
		}
	}

	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		BlockEntity tileEntity = worldIn.getBlockEntity(pos);
		if (tileEntity instanceof KitchenStoveTileEntity && state.getBlock() != newState.getBlock()) {
			KitchenStoveTileEntity te = (KitchenStoveTileEntity) tileEntity;
			ItemStack is = te.getItem(0);
			if (!is.isEmpty())
				super.popResource(worldIn, pos, is);
		}
		super.onRemove(state, worldIn, pos, newState, isMoving);
	}

	@Override
	protected void createBlockStateDefinition(net.minecraft.world.level.block.state.StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING).add(LIT).add(FUELED).add(ASH);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(LIT, false).setValue(ASH, false).setValue(FUELED, 0).setValue(FACING,
				context.getHorizontalDirection().getOpposite());

	}

	static final VoxelShape shape = Shapes.or(
			Shapes.or(Block.box(0, 0, 0, 16, 14, 16), Block.box(0, 14, 0, 2, 16, 16)),
			Shapes.or(Block.box(0, 14, 0, 16, 16, 2), Shapes
					.or(Block.box(14, 14, 0, 16, 16, 16), Block.box(0, 14, 14, 16, 16, 16))));

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos,
			CollisionContext context) {
		return shape;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return shape;
	}



}
