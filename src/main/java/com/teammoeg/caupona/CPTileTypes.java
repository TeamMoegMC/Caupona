package com.teammoeg.caupona;

import java.util.function.Supplier;

import com.google.common.collect.ImmutableSet;
import com.teammoeg.caupona.blocks.KitchenStoveT1;
import com.teammoeg.caupona.blocks.KitchenStoveT2;
import com.teammoeg.caupona.blocks.KitchenStoveTileEntity;
import com.teammoeg.caupona.blocks.BowlTileEntity;
import com.teammoeg.caupona.blocks.StewPotTileEntity;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CPTileTypes {
	public static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister
			.create(ForgeRegistries.BLOCK_ENTITIES, Main.MODID);

	public static final RegistryObject<BlockEntityType<StewPotTileEntity>> STEW_POT = REGISTER.register("stew_pot",
			makeType((p,s) -> new StewPotTileEntity(p,s), () -> CPBlocks.stew_pot));
	public static final RegistryObject<BlockEntityType<KitchenStoveTileEntity>> STOVE1 = REGISTER.register("kitchen_stove_t1",
			makeType((p,s) -> new KitchenStoveT1(p,s), () -> CPBlocks.stove1));
	public static final RegistryObject<BlockEntityType<KitchenStoveTileEntity>> STOVE2 = REGISTER.register("kitchen_stove_t2",
			makeType((p,s) -> new KitchenStoveT2(p,s), () -> CPBlocks.stove2));
	public static final RegistryObject<BlockEntityType<BowlTileEntity>> BOWL = REGISTER.register("bowl",
			makeType((p,s) -> new BowlTileEntity(p,s), () -> CPBlocks.bowl));
	
	private static <T extends BlockEntity> Supplier<BlockEntityType<T>> makeType(BlockEntitySupplier<T> create,
			Supplier<Block> valid) {
		return () -> new BlockEntityType<>(create,ImmutableSet.of(valid.get()), null);
	}

}