package com.teammoeg.caupona.blocks.foods;

import com.teammoeg.caupona.CPTileTypes;
import com.teammoeg.caupona.blocks.CPBaseTileBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemHandlerHelper;

public class DishBlock extends CPBaseTileBlock<DishTileEntity> {
	public static final IntegerProperty PAN=IntegerProperty.create("pan", 0, 2);
	public DishBlock(String name, Properties blockProps) {
		super(name, blockProps,CPTileTypes.DISH,null);
		this.registerDefaultState(this.defaultBlockState().setValue(PAN,0));
	}
	@Override
	protected void createBlockStateDefinition(
			net.minecraft.world.level.block.state.StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(PAN);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(PAN,0);
	}
	static final VoxelShape shape = Block.box(0, 0,0, 16, 3, 16);
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
	public boolean propagatesSkylightDown(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
		return true;
	}


	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return shape;
	}

	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		BlockEntity tileEntity = worldIn.getBlockEntity(pos);
		if (tileEntity instanceof DishTileEntity && state.getBlock() != newState.getBlock()) {
			DishTileEntity te = (DishTileEntity) tileEntity;
			//super.popResource(worldIn, pos, te.internal);
		}
		super.onRemove(state, worldIn, pos, newState, isMoving);
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn,
			BlockHitResult hit) {
		InteractionResult p = super.use(state, worldIn, pos, player, handIn, hit);
		if (p.consumesAction())
			return p;
		DishTileEntity tileEntity = (DishTileEntity) worldIn.getBlockEntity(pos);
		/*if (tileEntity.internal != null && tileEntity.internal.getItem() instanceof StewItem&&tileEntity.internal.isEdible()) {
			FoodProperties fp=tileEntity.internal.getFoodProperties(player);
			if(player.canEat(fp.canAlwaysEat())) {
				tileEntity.internal = player.eat(worldIn,tileEntity.internal);
				tileEntity.syncData();
			}
			return InteractionResult.SUCCESS;
		}*/
		return InteractionResult.PASS;
	}

	@Override
	public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
		super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
		BlockEntity tileEntity = pLevel.getBlockEntity(pPos);
		if (tileEntity instanceof BowlTileEntity) {
			BowlTileEntity te = (BowlTileEntity) tileEntity;
			te.internal = ItemHandlerHelper.copyStackWithSize(pStack, 1);
		}
	}

}
