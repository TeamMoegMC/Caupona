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

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPItems;
import com.teammoeg.caupona.Main;
import com.teammoeg.caupona.data.TranslationProvider;
import com.teammoeg.caupona.data.recipes.SauteedRecipe;
import com.teammoeg.caupona.data.recipes.StewBaseCondition;
import com.teammoeg.caupona.data.recipes.StewCookingRecipe;
import com.teammoeg.caupona.data.recipes.baseconditions.FluidTag;
import com.teammoeg.caupona.data.recipes.baseconditions.FluidType;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;

public class CPBookGenerator implements DataProvider {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
	private JsonParser jp = new JsonParser();
	protected final DataGenerator generator;
	private Path bookmain;
	private ExistingFileHelper helper;
	private Map<String, JsonObject> langs = new HashMap<>();
	private Map<String, StewCookingRecipe> recipes;
	private Map<String, SauteedRecipe> frecipes;

	class DatagenTranslationProvider implements TranslationProvider {
		String lang;

		public DatagenTranslationProvider(String lang) {
			super();
			this.lang = lang;
		}

		@Override
		public String getTranslation(String key, Object... objects) {
			if (langs.get(lang).has(key))
				return String.format(langs.get(lang).get(key).getAsString(), objects);
			return Utils.translate(key, objects).getString();
		}

	}

	public CPBookGenerator(DataGenerator generatorIn, ExistingFileHelper efh) {
		this.generator = generatorIn;
		this.helper = efh;
	}

	String[] allangs = { "zh_cn", "en_us", "es_es", "ru_ru" };

	@Override
	public void run(CachedOutput cache) throws IOException {
		bookmain = this.generator.getOutputFolder().resolve("data/" + Main.MODID + "/patchouli_books/book/");
		recipes = CPRecipeProvider.recipes.stream().filter(i -> i instanceof StewCookingRecipe)
				.map(e -> ((StewCookingRecipe) e))
				.collect(Collectors.toMap(e -> Utils.getRegistryName(e.output).getPath(), e -> e));
		frecipes = CPRecipeProvider.recipes.stream().filter(i -> i instanceof SauteedRecipe).map(e -> ((SauteedRecipe) e))
				.collect(Collectors.toMap(e -> Utils.getRegistryName(e.output).getPath(), e -> e));
		for (String lang : allangs)
			loadLang(lang);

		for (String s : CPItems.soups)
			if (helper.exists(new ResourceLocation(Main.MODID, "textures/gui/recipes/" + s + ".png"),
					PackType.CLIENT_RESOURCES))
				defaultPage(cache, s);
		for (String s : CPItems.dishes) {
			if (helper.exists(new ResourceLocation(Main.MODID, "textures/gui/recipes/" + s + ".png"),
					PackType.CLIENT_RESOURCES))
				defaultFryPage(cache, s);
		}
	}

	private void loadLang(String locale) {
		try {
			Resource rc = helper.getResource(new ResourceLocation(Main.MODID, "lang/" + locale + ".json"),
					PackType.CLIENT_RESOURCES);
			JsonObject jo = JsonParser.parseReader(new InputStreamReader(rc.open(), "UTF-8")).getAsJsonObject();
			langs.put(locale, jo);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String getName() {
		return Main.MODID + " recipe patchouli generator";
	}

	private void defaultPage(CachedOutput cache, String name) {
		for (String lang : allangs)
			saveEntry(name, lang, cache, createRecipe(name, lang));
	}

	private void defaultFryPage(CachedOutput cache, String name) {
		for (String lang : allangs)
			saveFryEntry(name, lang, cache, createFryingRecipe(name, lang));

	}

	StewBaseCondition anyW = new FluidTag(CPRecipeProvider.anyWater);
	StewBaseCondition stock = new FluidType(CPRecipeProvider.stock);
	StewBaseCondition milk = new FluidType(CPRecipeProvider.milk);

	private JsonObject createRecipe(String name, String locale) {
		JsonObject page = new JsonObject();
		page.add("name", langs.get(locale).get("item.caupona." + name));
		page.addProperty("icon", new ResourceLocation(Main.MODID, name).toString());
		page.addProperty("category", "caupona:cook_recipes");
		StewCookingRecipe r = recipes.get(name);
		Item baseType = CPItems.any.get();
		if (r.getBase() != null && !r.getBase().isEmpty()) {
			StewBaseCondition sbc = r.getBase().get(0);
			if (sbc.equals(anyW))
				baseType = CPItems.anyWater.get();
			else if (sbc.equals(stock))
				baseType = CPItems.stock.get();
			else if (sbc.equals(milk))
				baseType = CPItems.milk.get();
		}
		JsonArray pages = new JsonArray();
		JsonObject imgpage = new JsonObject();
		imgpage.addProperty("type", "caupona:cookrecipe");
		imgpage.addProperty("img",
				new ResourceLocation(r.getId().getNamespace(), "textures/gui/recipes/" + r.getId().getPath() + ".png").toString());
		imgpage.addProperty("result", new ResourceLocation(Main.MODID, name).toString());
		imgpage.addProperty("base", Utils.getRegistryName(baseType).toString());
		pages.add(imgpage);
		page.add("pages", pages);
		return page;
	}

	private JsonObject createFryingRecipe(String name, String locale) {
		JsonObject page = new JsonObject();
		page.add("name", langs.get(locale).get("item.caupona." + name));
		page.addProperty("icon", new ResourceLocation(Main.MODID, name).toString());
		page.addProperty("category", "caupona:sautee_recipes");
		SauteedRecipe r = frecipes.get(name);
		JsonArray pages = new JsonArray();
		JsonObject imgpage = new JsonObject();
		imgpage.addProperty("type", "caupona:fryrecipe");
		imgpage.addProperty("img",
				new ResourceLocation(r.getId().getNamespace(), "textures/gui/recipes/" + r.getId().getPath() + ".png").toString());
		imgpage.addProperty("result", new ResourceLocation(Main.MODID, name).toString());
		imgpage.addProperty("base", Utils.getRegistryName(CPBlocks.GRAVY_BOAT).toString());
		pages.add(imgpage);
		page.add("pages", pages);
		return page;
	}

	private void saveEntry(String name, String locale, CachedOutput cache, JsonObject entry) {
		saveJson(cache, entry, bookmain.resolve(locale + "/entries/recipes/" + name + ".json"));
	}

	private void saveFryEntry(String name, String locale, CachedOutput cache, JsonObject entry) {
		saveJson(cache, entry, bookmain.resolve(locale + "/entries/sautee_recipes/" + name + ".json"));
	}

	private static void saveJson(CachedOutput cache, JsonObject recipeJson, Path path) {
		try {
			DataProvider.saveStable(cache, recipeJson, path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error("Couldn't save data json {}", path, e);
		}

	}
}
