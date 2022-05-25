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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableSet;
import com.teammoeg.caupona.blocks.KitchenStove;
import com.teammoeg.caupona.blocks.KitchenStoveT1;
import com.teammoeg.caupona.blocks.KitchenStoveT2;
import com.teammoeg.caupona.blocks.KitchenStoveTileEntity;
import com.teammoeg.caupona.blocks.StewPot;
import com.teammoeg.caupona.blocks.StewPotTileEntity;
import com.teammoeg.caupona.container.KitchenStoveContainer;
import com.teammoeg.caupona.container.StewPotContainer;
import com.teammoeg.caupona.data.CPRecipeSerializer;
import com.teammoeg.caupona.data.recipes.BoilingRecipe;
import com.teammoeg.caupona.data.recipes.BowlContainingRecipe;
import com.teammoeg.caupona.data.recipes.CookingRecipe;
import com.teammoeg.caupona.data.recipes.CountingTags;
import com.teammoeg.caupona.data.recipes.DissolveRecipe;
import com.teammoeg.caupona.data.recipes.FluidFoodValueRecipe;
import com.teammoeg.caupona.data.recipes.FoodValueRecipe;
import com.teammoeg.caupona.items.IconItem;
import com.teammoeg.caupona.items.StewItem;
import com.teammoeg.caupona.items.CPBlockItem;
import com.teammoeg.caupona.items.CPItem;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Contents {

	public static List<Block> registeredBlocks = new ArrayList<>();
	public static List<Item> registeredItems = new ArrayList<>();
	public static List<Fluid> registeredFluids = new ArrayList<>();

	public static class CPBlocks {
		public static void init() {
		}

		public static Block stew_pot = new StewPot("stew_pot",
				Block.Properties.of(Material.STONE).sound(SoundType.STONE).requiresCorrectToolForDrops()
						.strength(2, 10).noOcclusion(),
						CPTileTypes.STEW_POT,
				CPBlockItem::new);
		public static Block stove1 = new KitchenStove("kitchen_stove_t1",Block.Properties.of(Material.STONE).sound(SoundType.STONE).requiresCorrectToolForDrops()
				.strength(2, 10).noOcclusion().lightLevel(s->s.getValue(KitchenStove.LIT)?7:0).isRedstoneConductor(CPBlocks::isntSolid).isSuffocating(CPBlocks::isntSolid).isViewBlocking(CPBlocks::isntSolid),
				CPTileTypes.STOVE1,
				CPBlockItem::new);
		public static Block stove2 = new KitchenStove("kitchen_stove_t2",Block.Properties.of(Material.STONE).sound(SoundType.STONE).requiresCorrectToolForDrops()
				.strength(2, 10).noOcclusion().lightLevel(s->s.getValue(KitchenStove.LIT)?9:0).isRedstoneConductor(CPBlocks::isntSolid).isSuffocating(CPBlocks::isntSolid).isViewBlocking(CPBlocks::isntSolid),
				CPTileTypes.STOVE2,
				CPBlockItem::new);
		   private static boolean isntSolid(BlockState state, BlockGetter reader, BlockPos pos) {
			      return false;
			   }
	}

	public static class CPItems {
		public static final String[] items = new String[] { "acquacotta", "bisque", "bone_gelatin", "borscht",
				"borscht_cream", "congee", "cream_of_meat_soup", "cream_of_mushroom_soup", "custard", "dilute_soup",
				"egg_drop_soup", "egg_tongsui", "fish_chowder", "fish_soup", "fricassee", "goji_tongsui", "goulash",
				"gruel", "hodgepodge", "meat_soup", "mushroom_soup", "nail_soup", "nettle_soup", "okroshka", "porridge",
				"poultry_soup", "pumpkin_soup", "pumpkin_soup_cream", "rice_pudding", "scalded_milk", "seaweed_soup",
				"stock", "stracciatella", "ukha", "vegetable_chowder", "vegetable_soup", "walnut_soup" };
		public static final List<Item> stews = new ArrayList<>();
		public static Item anyWater=new IconItem("water_or_stock_based");
		public static Item stock=new IconItem("stock_based");
		public static Item milk=new IconItem("milk_based");
		public static Item any=new IconItem("any_based");
		public static Item clay_pot=new CPItem("clay_cistern",new Item.Properties().tab(Main.itemGroup));
		public static void init() {
			for (String s : items)
				new StewItem(s, new ResourceLocation(Main.MODID, s), createProps());
			new StewItem("plain_milk", new ResourceLocation("milk"), createProps());
			new StewItem("plain_water", new ResourceLocation("water"), createProps());

		}

		static Properties createProps() {
			return new Item.Properties().tab(Main.itemGroup).craftRemainder(Items.BOWL).stacksTo(1);
		}
	}

	public static class CPTileTypes {
		public static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister
				.create(ForgeRegistries.BLOCK_ENTITIES, Main.MODID);

		public static final RegistryObject<BlockEntityType<StewPotTileEntity>> STEW_POT = REGISTER.register("stew_pot",
				makeType((p,s) -> new StewPotTileEntity(p,s), () -> CPBlocks.stew_pot));
		public static final RegistryObject<BlockEntityType<KitchenStoveTileEntity>> STOVE1 = REGISTER.register("kitchen_stove_t1",
				makeType((p,s) -> new KitchenStoveT1(p,s), () -> CPBlocks.stove1));
		public static final RegistryObject<BlockEntityType<KitchenStoveTileEntity>> STOVE2 = REGISTER.register("kitchen_stove_t2",
				makeType((p,s) -> new KitchenStoveT2(p,s), () -> CPBlocks.stove2));
		private static <T extends BlockEntity> Supplier<BlockEntityType<T>> makeType(BlockEntitySupplier<T> create,
				Supplier<Block> valid) {
			return () -> new BlockEntityType<>(create,ImmutableSet.of(valid.get()), null);
		}

	}

	public static class CPGui {
		public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister
				.create(ForgeRegistries.CONTAINERS, Main.MODID);
		public static final RegistryObject<MenuType<StewPotContainer>> STEWPOT = CONTAINERS.register("stew_pot",
				() -> IForgeMenuType.create(StewPotContainer::new));
		public static final RegistryObject<MenuType<KitchenStoveContainer>> STOVE = CONTAINERS.register("kitchen_stove",
				() -> IForgeMenuType.create(KitchenStoveContainer::new));
	}

	public static class CPRecipes {
		public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister
				.create(ForgeRegistries.RECIPE_SERIALIZERS, Main.MODID);

		static {
			CookingRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("cooking",
					() -> new CPRecipeSerializer<CookingRecipe>(CookingRecipe::new, CookingRecipe::new,
							CookingRecipe::write));
			BoilingRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("boiling",
					() -> new CPRecipeSerializer<BoilingRecipe>(BoilingRecipe::new, BoilingRecipe::new,
							BoilingRecipe::write));
			BowlContainingRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("bowl",
					() -> new CPRecipeSerializer<BowlContainingRecipe>(BowlContainingRecipe::new,
							BowlContainingRecipe::new, BowlContainingRecipe::write));
			DissolveRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("dissolve",
					() -> new CPRecipeSerializer<DissolveRecipe>(DissolveRecipe::new, DissolveRecipe::new,
							DissolveRecipe::write));
			CountingTags.SERIALIZER = RECIPE_SERIALIZERS.register("tags",
					() -> new CPRecipeSerializer<CountingTags>(CountingTags::new, CountingTags::new,
							CountingTags::write));
			FoodValueRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("food",
					() -> new CPRecipeSerializer<FoodValueRecipe>(FoodValueRecipe::new, FoodValueRecipe::new,
							FoodValueRecipe::write));
			FluidFoodValueRecipe.SERIALIZER=RECIPE_SERIALIZERS.register("fluid_food",() ->new CPRecipeSerializer<FluidFoodValueRecipe>(FluidFoodValueRecipe::new, FluidFoodValueRecipe::new,
					FluidFoodValueRecipe::write));
			
		}

		public static void registerRecipeTypes() {
			CookingRecipe.TYPE = RecipeType.register(Main.MODID + ":stew");
			BoilingRecipe.TYPE = RecipeType.register(Main.MODID + ":boil");
			BowlContainingRecipe.TYPE = RecipeType.register(Main.MODID + ":bowl");
			DissolveRecipe.TYPE = RecipeType.register(Main.MODID + ":dissolve");
			CountingTags.TYPE = RecipeType.register(Main.MODID + ":tags");
			FoodValueRecipe.TYPE = RecipeType.register(Main.MODID + ":food");
			FluidFoodValueRecipe.TYPE = RecipeType.register(Main.MODID + ":fluid_food");
		}
	}
}
