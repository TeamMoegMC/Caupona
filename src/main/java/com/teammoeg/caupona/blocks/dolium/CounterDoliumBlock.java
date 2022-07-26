package com.teammoeg.caupona.blocks.dolium;

import java.util.Random;

import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPTileTypes;
import com.teammoeg.caupona.blocks.CPHorizontalBlock;
import com.teammoeg.caupona.blocks.CPHorizontalTileBlock;
import com.teammoeg.caupona.blocks.pot.StewPotTileEntity;
import com.teammoeg.caupona.client.Particles;
import com.teammoeg.caupona.data.recipes.BowlContainingRecipe;
import com.teammoeg.caupona.items.StewItem;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.network.NetworkHooks;

public class CounterDoliumBlock extends CPHorizontalTileBlock<CounterDoliumTileEntity> implements LiquidBlockContainer {

	public CounterDoliumBlock(Properties p) {
		super(CPTileTypes.DOLIUM, p);
		CPBlocks.dolium.add(this);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public float getShadeBrightness(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 1.0F;
	}

	@Override
	public boolean useShapeForLightOcclusion(BlockState state) {
		return true;
	}

	static final VoxelShape shape = Shapes.or(Shapes.or(Block.box(0, 0, 0, 16, 4, 16), Block.box(0, 4, 0, 4, 16, 16)),
			Shapes.or(Block.box(0, 4, 0, 16, 16, 4),
					Shapes.or(Block.box(12, 4, 0, 16, 16, 16), Block.box(0, 4, 12, 16, 16, 16))));

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return shape;
	}

	public VoxelShape getVisualShape(BlockState pState, BlockGetter pReader, BlockPos pPos, CollisionContext pContext) {
		return Shapes.empty();
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn,
			BlockHitResult hit) {
		InteractionResult p = super.use(state, worldIn, pos, player, handIn, hit);
		if (p.consumesAction())
			return p;
		CounterDoliumTileEntity tileEntity = (CounterDoliumTileEntity) worldIn.getBlockEntity(pos);
		
		ItemStack held = player.getItemInHand(handIn);
		if (held.isEmpty() && player.isShiftKeyDown()) {
			tileEntity.tank.setFluid(FluidStack.EMPTY);
			return InteractionResult.SUCCESS;
		}
		if (held.getItem() instanceof StewItem) {
			if (tileEntity.tryAddFluid(BowlContainingRecipe.extractFluid(held))) {
				ItemStack ret = held.getContainerItem();
				held.shrink(1);
				if (!player.addItem(ret))
					player.drop(ret, false);
			}

			return InteractionResult.sidedSuccess(worldIn.isClientSide);
		}
		if (FluidUtil.interactWithFluidHandler(player, handIn, tileEntity.tank))
			return InteractionResult.SUCCESS;

		
		if (handIn == InteractionHand.MAIN_HAND) {
			if (tileEntity != null && !worldIn.isClientSide)
				NetworkHooks.openGui((ServerPlayer) player, tileEntity, tileEntity.getBlockPos());
			return InteractionResult.SUCCESS;
		}
		return p;
	}

	@Override
	public boolean canPlaceLiquid(BlockGetter w, BlockPos p, BlockState s, Fluid f) {
		CounterDoliumTileEntity te = (CounterDoliumTileEntity) w.getBlockEntity(p);
		return te.tank.fill(new FluidStack(f,1000),FluidAction.SIMULATE)==1000;
	}

	@Override
	public boolean placeLiquid(LevelAccessor w, BlockPos p, BlockState s, FluidState f) {
		CounterDoliumTileEntity te = (CounterDoliumTileEntity) w.getBlockEntity(p);
		if (te.tryAddFluid(new FluidStack(f.getType(), 1000))) {
			return true;
		}
		return false;
	}
	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		BlockEntity tileEntity = worldIn.getBlockEntity(pos);
		if (tileEntity instanceof CounterDoliumTileEntity && state.getBlock() != newState.getBlock()) {
			CounterDoliumTileEntity te = (CounterDoliumTileEntity) tileEntity;

			for (int i = 0; i < 6; i++) {
				ItemStack is = te.inv.getStackInSlot(i);
				if (!is.isEmpty())
					super.popResource(worldIn, pos, is);
			}
		}
		super.onRemove(state, worldIn, pos, newState, isMoving);
	}

}
