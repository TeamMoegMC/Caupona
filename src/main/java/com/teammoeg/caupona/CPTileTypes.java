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

package com.teammoeg.caupona;

import java.util.function.Supplier;

import com.google.common.collect.ImmutableSet;
import com.teammoeg.caupona.blocks.dolium.CounterDoliumTileEntity;
import com.teammoeg.caupona.blocks.foods.BowlTileEntity;
import com.teammoeg.caupona.blocks.foods.DishTileEntity;
import com.teammoeg.caupona.blocks.fumarole.FumaroleVentTileEntity;
import com.teammoeg.caupona.blocks.hypocast.CaliductTile;
import com.teammoeg.caupona.blocks.hypocast.FireboxTile;
import com.teammoeg.caupona.blocks.hypocast.WolfStatueTile;
import com.teammoeg.caupona.blocks.others.CPSignTileEntity;
import com.teammoeg.caupona.blocks.pan.PanTile;
import com.teammoeg.caupona.blocks.pot.StewPotTileEntity;
import com.teammoeg.caupona.blocks.stove.ChimneyPotTileEntity;
import com.teammoeg.caupona.blocks.stove.KitchenStoveT1;
import com.teammoeg.caupona.blocks.stove.KitchenStoveT2;
import com.teammoeg.caupona.blocks.stove.KitchenStoveTileEntity;

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
			makeType(StewPotTileEntity::new, () -> CPBlocks.stew_pot));
	public static final RegistryObject<BlockEntityType<KitchenStoveTileEntity>> STOVE1 = REGISTER.register("kitchen_stove_basic",
			makeType(KitchenStoveT1::new, () -> CPBlocks.stove1));
	public static final RegistryObject<BlockEntityType<KitchenStoveTileEntity>> STOVE2 = REGISTER.register("kitchen_stove_fast",
			makeTypes(KitchenStoveT2::new, () -> new Block[]{CPBlocks.stove2,CPBlocks.stove3,CPBlocks.stove4,CPBlocks.stove5}));
	public static final RegistryObject<BlockEntityType<BowlTileEntity>> BOWL = REGISTER.register("bowl",
			makeType(BowlTileEntity::new, () -> CPBlocks.bowl));
	public static final RegistryObject<BlockEntityType<CPSignTileEntity>> SIGN = REGISTER.register("sign",
			makeTypes(CPSignTileEntity::new, () -> CPBlocks.signs.toArray(new Block[0])));
	public static final RegistryObject<BlockEntityType<ChimneyPotTileEntity>> CHIMNEY = REGISTER.register("chimney_pot",
			makeTypes(ChimneyPotTileEntity::new, () -> CPBlocks.chimney.toArray(new Block[0])));
	public static final RegistryObject<BlockEntityType<FumaroleVentTileEntity>> FUMAROLE = REGISTER.register("fumarole_vent",
			makeType(FumaroleVentTileEntity::new, () -> CPBlocks.FUMAROLE_VENT));
	public static final RegistryObject<BlockEntityType<PanTile>> PAN = REGISTER.register("pan",
			makeTypes(PanTile::new, () -> new Block[]{CPBlocks.STONE_PAN,CPBlocks.COPPER_PAN,CPBlocks.IRON_PAN}));
	public static final RegistryObject<BlockEntityType<CounterDoliumTileEntity>> DOLIUM = REGISTER.register("dolium",
			makeTypes(CounterDoliumTileEntity::new, () -> CPBlocks.dolium.toArray(new Block[0])));
	public static final RegistryObject<BlockEntityType<DishTileEntity>> DISH = REGISTER.register("dish",
			makeTypes(DishTileEntity::new, () -> CPBlocks.dishes.toArray(new Block[0])));
	public static final RegistryObject<BlockEntityType<CaliductTile>> CALIDUCT = REGISTER.register("caliduct",
			makeTypes(CaliductTile::new, () -> CPBlocks.caliduct.toArray(new Block[0])));
	public static final RegistryObject<BlockEntityType<FireboxTile>> FIREBOX = REGISTER.register("hypocast_firebox",
			makeTypes(FireboxTile::new, () -> CPBlocks.firebox.toArray(new Block[0])));
	public static final RegistryObject<BlockEntityType<WolfStatueTile>> WOLF = REGISTER.register("wolf_statue",
			makeType(WolfStatueTile::new, () -> CPBlocks.WOLF));
	
	
	private static <T extends BlockEntity> Supplier<BlockEntityType<T>> makeType(BlockEntitySupplier<T> create,
			Supplier<Block> valid) {
		return () -> new BlockEntityType<>(create,ImmutableSet.of(valid.get()), null);
	}
	private static <T extends BlockEntity> Supplier<BlockEntityType<T>> makeTypes(BlockEntitySupplier<T> create,
			Supplier<Block[]> valid) {
		return () -> new BlockEntityType<>(create,ImmutableSet.copyOf(valid.get()), null);
	}
}