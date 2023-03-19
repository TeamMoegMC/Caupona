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
 * Specially, we allow this software to be used alongside with closed source software Minecraft(R) and Forge or other modloader.
 * Any mods or plugins can also use apis provided by forge or com.teammoeg.caupona.api without using GPL or open source.
 *
 * You should have received a copy of the GNU General Public License
 * along with Caupona. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.caupona.datagen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPFluids;
import com.teammoeg.caupona.CPItems;
import com.teammoeg.caupona.Main;
import com.teammoeg.caupona.data.IDataRecipe;
import com.teammoeg.caupona.data.recipes.AspicMeltingRecipe;
import com.teammoeg.caupona.data.recipes.BoilingRecipe;
import com.teammoeg.caupona.data.recipes.BowlContainingRecipe;
import com.teammoeg.caupona.data.recipes.DoliumRecipe;
import com.teammoeg.caupona.data.recipes.FluidFoodValueRecipe;
import com.teammoeg.caupona.data.recipes.FoodValueRecipe;
import com.teammoeg.caupona.data.recipes.SpiceRecipe;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.ForgeRegistries;

public class CPRecipeProvider extends RecipeProvider {
	private final HashMap<String, Integer> PATH_COUNT = new HashMap<>();
	static final ResourceLocation rice = mrl("cereals/rice"), eggs = mrl("eggs"), baked = mrl("cereals/baked"),
			anyWater = mrl("water"), vegetables = mrl("vegetables"), meat = mrl("meats/meat"),
			fish = mrl("seafood/fish"), poultry = mrl("meats/poultry"), seafood = mrl("seafood"), meats = mrl("meats"),
			sugar = mrl("sugar"), cereals = mrl("cereals"), crustaceans = mrl("seafood/crustaceans"),
			roots = mrl("vegetables/roots"), mushrooms = mrl("mushroom"), pumpkin = mrl("vegetables/pumpkin"),
			walnut = mrl("walnut"), greens = mrl("vegetables/greens");
	static final Fluid water = fluid(mrl("nail_soup")), milk = fluid(mrl("scalded_milk")), stock = fluid(mrl("stock"));
	public static List<IDataRecipe> recipes = new ArrayList<>();

	public CPRecipeProvider(DataGenerator generatorIn) {
		super(generatorIn.getPackOutput());
	}

	@Override
	protected void buildRecipes(Consumer<FinishedRecipe> outx) {
		Consumer<IDataRecipe> out = r -> {
			outx.accept(new FinishedRecipe() {

				@Override
				public void serializeRecipeData(JsonObject jo) {
					r.serializeRecipeData(jo);
				}

				@Override
				public ResourceLocation getId() {
					return r.getId();
				}

				@Override
				public JsonObject serializeAdvancement() {
					return null;
				}

				@Override
				public ResourceLocation getAdvancementId() {
					return null;
				}

				@Override
				public RecipeSerializer<?> getType() {
					return r.getSerializer();
				}

			});
		};
		for (String s : CPFluids.getSoupfluids()) {
			ResourceLocation fs = mrl(s);
			out.accept(new BowlContainingRecipe(rl("bowl/" + s), item(fs), fluid(fs)));
		}
		// out.accept(dissolve(RankineItems.CORN_EAR.get()));
		out.accept(new BowlContainingRecipe(rl("bowl/water"), cpitem("water"), Fluids.WATER));
		out.accept(new BowlContainingRecipe(rl("bowl/milk"), cpitem("milk"), ForgeMod.MILK.get()));
		out.accept(new BoilingRecipe(rl("boil/water"), fluid(mcrl("water")), fluid(mrl("nail_soup")), 200));
		out.accept(new BoilingRecipe(rl("boil/milk"), fluid(mcrl("milk")), fluid(mrl("scalded_milk")), 200));
		out.accept(new FoodValueRecipe(rl("food/mushroom"), 3, 0.6f, new ItemStack(Items.RED_MUSHROOM),
				Items.RED_MUSHROOM, Items.BROWN_MUSHROOM));
		out.accept(new FoodValueRecipe(rl("food/pumpkin"), 3, 0.6f, new ItemStack(Items.PUMPKIN), Items.PUMPKIN,
				Items.CARVED_PUMPKIN));
		out.accept(new FoodValueRecipe(rl("food/wheat"), 3, 0.6f, new ItemStack(Items.WHEAT), Items.WHEAT,
				Items.WHEAT_SEEDS));
		out.accept(
				new FoodValueRecipe(rl("food/fern"), 2, 0.3f, new ItemStack(Items.FERN), Items.FERN, Items.LARGE_FERN));
		out.accept(new FoodValueRecipe(rl("food/allium"), 1, 0.2f, new ItemStack(Items.ALLIUM), Items.ALLIUM));
		// System.out.println(CPBlocks.stove1.asItem());
		// System.out.println(CPBlocks.stove1.asItem().getItemCategory());
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS,cpitem("mud_kitchen_stove")).define('D', Items.DIRT).define('S', Items.COBBLESTONE)
				.pattern("DDD").pattern("SSS").pattern("S S").unlockedBy("has_cobblestone", has(Blocks.COBBLESTONE))
				.save(outx);
		// ShapedRecipeBuilder.shaped(CPBlocks.stove2).define('T',Items.BRICK_SLAB).define('B',Items.BRICKS).define('C',Items.CLAY).pattern("TTT").pattern("BCB").pattern("B
		// B").unlockedBy("has_bricks", has(Blocks.BRICKS)).save(outx);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS,CPItems.clay_pot.get()).define('C', Items.CLAY_BALL).define('S', Items.STICK)
				.pattern("CCC").pattern("CSC").pattern("CCC").unlockedBy("has_clay", has(Items.CLAY_BALL)).save(outx);
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(CPItems.clay_pot.get()),RecipeCategory.DECORATIONS, CPBlocks.stew_pot.get(), 0.35f, 200)
				.unlockedBy("has_claypot", has(CPItems.clay_pot.get())).save(outx);
		// ShapedRecipeBuilder.shapedRecipe(THPBlocks.stew_pot).key('B',Items.BRICK).key('C',Items.CLAY_BALL).patternLine("BCB").patternLine("B
		// B").patternLine("BBB").unlockedBy("has_brick",
		// hasItem(Items.BRICK)).build(out);
		// ShapelessRecipeBuilder.shapelessRecipe(THPItems.BOOK).addIngredient(Items.BOOK).addIngredient(Items.BOWL).unlockedBy("has_bowl",
		// hasItem(Items.BOWL)).build(out);
		out.accept(new FluidFoodValueRecipe(rl("fluid_food/milk"), 0, 1f, new ItemStack(Items.MILK_BUCKET), 4,
				new ResourceLocation(Main.MODID, "scalded_milk")));
		out.accept(new FluidFoodValueRecipe(rl("fluid_food/stock"), 2, 1f, null, 4,
				new ResourceLocation(Main.MODID, "stock")));
		simpleFood(out, 2, 0.4f, Items.HONEYCOMB);
		/*
		 * simpleFood(out,3,5f,ItemRegistry.amaranthitem);
		 * simpleFood(out,3,5f,ItemRegistry.barleyitem);
		 * simpleFood(out,3,5f,ItemRegistry.beanitem);
		 * simpleFood(out,3,5f,ItemRegistry.chickpeaitem);
		 * simpleFood(out,3,5f,ItemRegistry.cornitem);
		 * simpleFood(out,3,5f,ItemRegistry.lentilitem);
		 * simpleFood(out,3,5f,ItemRegistry.milletitem);
		 * simpleFood(out,3,5f,ItemRegistry.oatsitem);
		 * simpleFood(out,3,5f,ItemRegistry.quinoaitem);
		 * simpleFood(out,3,5f,ItemRegistry.riceitem);
		 * simpleFood(out,3,5f,ItemRegistry.ryeitem);
		 * simpleFood(out,3,5f,ItemRegistry.soybeanitem);
		 */
		simpleFood(out, 0, .5f, Items.BONE_MEAL);
		simpleFood(out, 1, .5f, Items.BONE);
		simpleFood(out, 3, .5f, Items.EGG);
		for (String s : ImmutableSet.of("bisque", "borscht", "dilute_soup", "egg_drop_soup", "fish_soup", "goulash",
				"hodgepodge", "meat_soup", "mushroom_soup", "nettle_soup", "poultry_soup", "pumpkin_soup",
				"seaweed_soup", "stock", "stracciatella", "vegetable_soup")) {
			aspic(s, out);
		}
		spice(cpitem("garum_spice_jar"), MobEffects.JUMP, out);
		spice(cpitem("sugar_spice_jar"), MobEffects.MOVEMENT_SPEED, out);
		spice(cpitem("chives_spice_jar"), MobEffects.SLOW_FALLING, out);
		spice(cpitem("vinegar_spice_jar"), MobEffects.NIGHT_VISION, out);
		stewCooking(out);
		frying(out);
		out.accept(new DoliumRecipe(new ResourceLocation(Main.MODID, "dolium/garum_spice_jar"), null, Fluids.EMPTY, 0,
				0f, false, new ItemStack(cpitem("garum_spice_jar")),
				Arrays.asList(
						Pair.of(Ingredient.of(ItemTags.create(new ResourceLocation(Main.MODID, "garum_fish"))), 4)),
				Ingredient.of(Items.FLOWER_POT)));
		out.accept(new DoliumRecipe(new ResourceLocation(Main.MODID, "dolium/vinegar_spice_jar_from_fruits"), null,
				Fluids.EMPTY, 0, 0f, false, new ItemStack(cpitem("vinegar_spice_jar")),
				Arrays.asList(
						Pair.of(Ingredient.of(ItemTags.create(new ResourceLocation(Main.MODID, "vinegar_fruits"))), 4)),
				Ingredient.of(Items.FLOWER_POT)));
		out.accept(new DoliumRecipe(new ResourceLocation(Main.MODID, "dolium/vinegar_spice_jar_from_berries"), null,
				Fluids.EMPTY, 0, 0f, false, new ItemStack(cpitem("vinegar_spice_jar")),
				Arrays.asList(Pair.of(
						Ingredient.of(ItemTags.create(new ResourceLocation(Main.MODID, "vinegar_fruits_small"))), 16)),
				Ingredient.of(Items.FLOWER_POT)));
		out.accept(new DoliumRecipe(new ResourceLocation(Main.MODID, "dolium/gravy_boat"), null, Fluids.EMPTY, 0, 0f,
				false, new ItemStack(CPItems.gravy_boat.get()),
				Arrays.asList(Pair.of(Ingredient.of(ItemTags.create(new ResourceLocation(Main.MODID, "walnut"))), 8),
						Pair.of(Ingredient.of(ItemTags.ANVIL), 0)),
				Ingredient.of(CPItems.gravy_boat.get())));
		out.accept(new DoliumRecipe(new ResourceLocation(Main.MODID, "dolium/gravy_boat_glass_bottle"), null,
				Fluids.EMPTY, 0, 0f, false, new ItemStack(CPItems.gravy_boat.get()),
				Arrays.asList(Pair.of(Ingredient.of(ItemTags.create(new ResourceLocation(Main.MODID, "walnut"))), 8),
						Pair.of(Ingredient.of(ItemTags.ANVIL), 0)),
				Ingredient.of(Items.GLASS_BOTTLE)));
		out.accept(new DoliumRecipe(new ResourceLocation(Main.MODID, "dolium/vivid_charcoal"), null, Fluids.LAVA, 250,
				0f, false, new ItemStack(cpitem("vivid_charcoal"), 8),
				Arrays.asList(Pair.of(Ingredient.of(ItemTags.COALS), 3), Pair.of(Ingredient.of(Items.SLIME_BALL), 1)),
				null));

	}

	private void frying(Consumer<IDataRecipe> out) {
		out = out.andThen(recipes::add);
		fry("huevos_pericos").high().require().mainly().of(eggs).and().then().finish(out);
		fry("sauteed_beef").high().require().mainly().of(ftag("raw_beef")).and().then().finish(out);
		fry("sauteed_greens").high().require().mainly().of(greens).and().then().finish(out);
		fry("sauteed_meat").med().require().mainly().of(meat).and().then().finish(out);
		fry("sauteed_mushrooms").med().require().mainly().of(mushrooms).and().then().finish(out);
		fry("sauteed_roots").high().require().mainly().of(roots).and().then().finish(out);
		fry("sauteed_seafood").med().require().mainly().of(seafood).and().then().finish(out);
		fry("sauteed_vegetables").med().require().mainly().of(vegetables).and().then().finish(out);
		fry("seared_fillet").med().require().mainly().of(fish).and().then().finish(out);
		fry("seared_poultry").high().require().mainly().of(poultry).and().then().finish(out);
		fry("sauteed_hodgepodge").low().finish(out);
	}

	private void stewCooking(Consumer<IDataRecipe> out) {
		out = out.andThen(recipes::add);
		cook("acquacotta").high().base().tag(anyWater).and().require().mainly().of(baked).and().then().finish(out);
		cook("congee").med().base().tag(anyWater).and().require().half().of(rice).and().then().dense(0.25).finish(out);
		cook("rice_pudding").med().base().type(milk).and().require().half().of(rice).and().then().dense(0.25)
				.finish(out);
		cook("gruel").base().tag(anyWater).and().require().half().of(cereals).and().then().dense(0.25).finish(out);
		cook("porridge").base().type(milk).and().require().half().of(cereals).and().then().dense(0.25).finish(out);
		cook("egg_drop_soup").base().tag(anyWater).and().require().mainly().of(eggs).and().not().any().of(vegetables)
				.and().then().dense(0.5).finish(out);
		cook("stracciatella").base().tag(anyWater).and().require().mainly().of(eggs).and().any().of(vegetables).and()
				.then().dense(0.5).finish(out);
		cook("custard").base().type(milk).and().require().mainly().of(eggs).and().then().dense(0.5).finish(out);
		cook("vegetable_soup").base().tag(anyWater).and().require().mainly().of(vegetables).and().then().finish(out);
		cook("vegetable_chowder").base().type(milk).and().require().mainly().of(vegetables).and().then().finish(out);
		cook("borscht").high().base().tag(anyWater).and().require().mainly().of(vegetables).and().typeMainly(vegetables)
				.of(Items.BEETROOT).and().then().finish(out);
		cook("borscht_cream").high().base().type(milk).and().require().mainly().of(vegetables).and()
				.typeMainly(vegetables).of(Items.BEETROOT).and().then().finish(out);
		cook("pumpkin_soup").high().base().tag(anyWater).and().require().mainly().of(vegetables).and()
				.typeMainly(vegetables).of(pumpkin).and().then().finish(out);
		cook("pumpkin_soup_cream").high().base().type(milk).and().require().mainly().of(vegetables).and()
				.typeMainly(vegetables).of(pumpkin).and().then().finish(out);
		cook("mushroom_soup").high().base().tag(anyWater).and().require().mainly().of(vegetables).and()
				.typeMainly(vegetables).of(mushrooms).and().then().finish(out);
		cook("cream_of_mushroom_soup").high().base().type(milk).and().require().mainly().of(vegetables).and()
				.typeMainly(vegetables).of(mushrooms).and().then().finish(out);
		cook("seaweed_soup").med().base().tag(anyWater).and().require().mainly().of(Items.KELP).and().then()
				.finish(out);
		cook("bisque").base().tag(anyWater).and().require().mainly().of(crustaceans).and().then().finish(out);
		cook("fish_soup").base().tag(anyWater).and().require().mainly().of(fish).and().then().finish(out);
		cook("fish_chowder").base().type(milk).and().require().mainly().of(seafood).and().then().finish(out);
		cook("poultry_soup").base().tag(anyWater).and().require().mainly().of(poultry).and().then().finish(out);
		cook("fricassee").base().type(milk).and().require().mainly().of(poultry).and().then().finish(out);
		cook("meat_soup").base().tag(anyWater).and().require().mainly().of(meat).and().then().finish(out);
		cook("cream_of_meat_soup").base().type(milk).and().require().mainly().of(meat).and().then().finish(out);
		cook("hodgepodge").prio(-1).finish(out);
		cook("dilute_soup").prio(-2).dense(0).finish(out);

		cook("stock").special().base().type(water).and().require().mainly().of(mrl("bone")).plus(poultry).and().any()
				.of(mrl("bone")).of(poultry).and().then().finish(out);
		// cook("bone_gelatin").special().high().base().type(water).and().require().half().of(Items.BONE_MEAL).and().then()
		// .dense(3).finish(out);
		cook("egg_tongsui").special().med().base().type(water).and().require().half().of(eggs).and().any().of(sugar)
				.and().not().any().of(meats).of(seafood).of(vegetables).of(mrl("wolfberries")).and().then().finish(out);
		cook("walnut_soup").special().med().base().type(water).and().require().half().of(walnut).and().any().of(sugar)
				.and().not().any().of(meats).of(seafood).of(vegetables).of(mrl("wolfberries")).and().then().finish(out);
		cook("goji_tongsui").special().med().base().type(water).and().require().mainly().of(sugar).and().any()
				.of(mrl("wolfberries")).and().not().any().of(meats).of(seafood).of(vegetables).and().then().finish(out);
		cook("ukha").special().med().base().type(water).and().require().half().of(fish).plus(roots).and().any()
				.of(fish).of(roots).and().not().any().of(meats).of(cereals).and().then().finish(out);
		cook("goulash").special().high().base().type(stock).and().require().mainly().of(ftag("raw_beef")).and().any()
				.of(vegetables).and().not().any().of(seafood).of(cereals).and().then().finish(out);
		cook("okroshka").special().high().require().half().of(vegetables).plus(meats).and().any().of(vegetables)
				.of(meats).of(mrl("ice")).and().then().finish(out);
		cook("nettle_soup").special().med().require().half().of(mrl("fern")).and().not().any().of(seafood).of(meats)
				.of(cereals).and().then().finish(out);
		cook("bone_gelatin").special().med().require().only().of(Items.BONE).and().then().dense(1).finish(out);
	}

	private void spice(Item spice, MobEffect eff, Consumer<IDataRecipe> out) {
		out.accept(new SpiceRecipe(new ResourceLocation(Main.MODID, "spice/" + Utils.getRegistryName(spice).getPath()),
				Ingredient.of(spice), new MobEffectInstance(eff, 200)));

	}

	private void aspic(String soup, Consumer<IDataRecipe> out) {
		out.accept(
				new DoliumRecipe(new ResourceLocation(Main.MODID, "dolium/" + soup + "_aspic"), Utils.getRegistryName(stock),
						cpfluid(soup), 250, 0.25F, true, new ItemStack(cpitem(soup + "_aspic")), null));
		out.accept(new AspicMeltingRecipe(new ResourceLocation(Main.MODID, "melt/" + soup + "_aspic"),
				Ingredient.of(cpitem(soup + "_aspic")), cpfluid(soup)));
	}

	private Fluid cpfluid(String name) {
		return ForgeRegistries.FLUIDS.getValue(new ResourceLocation(Main.MODID, name));
	}

	private Item cpitem(String name) {
		return ForgeRegistries.ITEMS.getValue(new ResourceLocation(Main.MODID, name));
	}

	private Item mitem(String name) {
		return ForgeRegistries.ITEMS.getValue(new ResourceLocation(name));
	}

	private void simpleFood(Consumer<IDataRecipe> out, int h, float s, Item i) {
		out.accept(new FoodValueRecipe(rl("food/" + Utils.getRegistryName(i).getPath()), h, s, new ItemStack(i), i));
	}

	private StewRecipeBuilder cook(String s) {
		return StewRecipeBuilder.start(fluid(mrl(s)));
	}

	private SauteedRecipeBuilder fry(String s) {
		return SauteedRecipeBuilder.start(item(mrl(s)));
	}

	private Item item(ResourceLocation rl) {
		return ForgeRegistries.ITEMS.getValue(rl);
	}

	private static Fluid fluid(ResourceLocation rl) {
		return ForgeRegistries.FLUIDS.getValue(rl);
	}

	private static ResourceLocation mrl(String s) {
		return new ResourceLocation(Main.MODID, s);
	}

	private ResourceLocation ftag(String s) {
		return new ResourceLocation("forge", s);
	}

	private ResourceLocation mcrl(String s) {
		return new ResourceLocation(s);
	}

	private ResourceLocation rl(String s) {
		if (!s.contains("/"))
			s = "crafting/" + s;
		if (PATH_COUNT.containsKey(s)) {
			int count = PATH_COUNT.get(s) + 1;
			PATH_COUNT.put(s, count);
			return new ResourceLocation(Main.MODID, s + count);
		}
		PATH_COUNT.put(s, 1);
		return new ResourceLocation(Main.MODID, s);
	}


}
