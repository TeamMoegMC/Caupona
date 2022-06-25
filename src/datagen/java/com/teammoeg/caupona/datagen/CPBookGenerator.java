/*
+	 * Copyright (c) 2022 TeamMoeg
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

package com.teammoeg.caupona.datagen;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.teammoeg.caupona.Main;
import com.teammoeg.caupona.Contents.CPItems;
import com.teammoeg.caupona.data.TranslationProvider;
import com.teammoeg.caupona.data.recipes.CookingRecipe;
import com.teammoeg.caupona.data.recipes.StewBaseCondition;
import com.teammoeg.caupona.data.recipes.baseconditions.FluidTag;
import com.teammoeg.caupona.data.recipes.baseconditions.FluidType;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;

public class CPBookGenerator implements DataProvider {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
	private JsonParser jp=new JsonParser();
	protected final DataGenerator generator;
	private Path bookmain;
	private ExistingFileHelper helper;
	private Map<String,JsonObject> langs=new HashMap<>();
	private Map<String,CookingRecipe> recipes;
	class DatagenTranslationProvider implements TranslationProvider{
		String lang;
		public DatagenTranslationProvider(String lang) {
			super();
			this.lang = lang;
		}
		@Override
		public String getTranslation(String key, Object... objects) {
			if(langs.get(lang).has(key))
				return String.format(langs.get(lang).get(key).getAsString(),objects);
			return new TranslatableComponent(key,objects).getString();
		}
		
	}

	public CPBookGenerator(DataGenerator generatorIn,ExistingFileHelper efh) {
		this.generator = generatorIn;
		this.helper=efh;
	}

	@Override
	public void run(HashCache cache) throws IOException {
		bookmain=this.generator.getOutputFolder().resolve("data/"+Main.MODID+"/patchouli_books/book/");
		recipes=CPRecipeProvider.recipes.stream().map(e->((CookingRecipe)e)).collect(Collectors.toMap(e->e.output.getRegistryName().getPath(),e->e));
		loadLang("zh_cn");
		loadLang("en_us");
		for(String s:CPItems.items)
			if(helper.exists(new ResourceLocation(Main.MODID,"textures/gui/recipes/"+s+".png"),PackType.CLIENT_RESOURCES))
			defaultPage(cache,s);
	}
	private void loadLang(String locale) {
		try {
			Resource rc=helper.getResource(new ResourceLocation(Main.MODID,"lang/"+locale+".json"),PackType.CLIENT_RESOURCES);
			JsonObject jo=jp.parse(new InputStreamReader(rc.getInputStream(),"UTF-8")).getAsJsonObject();
			langs.put(locale,jo);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public String getName() {
		return Main.MODID+" recipe patchouli generator";
	}
	private void defaultPage(HashCache cache,String name) {
		saveEntry(name,"en_us",cache,createRecipe(name,"en_us"));
		saveEntry(name,"zh_cn",cache,createRecipe(name,"zh_cn"));
	}
	StewBaseCondition anyW=new FluidTag(CPRecipeProvider.anyWater);
	StewBaseCondition stock=new FluidType(CPRecipeProvider.stock);
	StewBaseCondition milk=new FluidType(CPRecipeProvider.milk);
	private JsonObject createRecipe(String name,String locale) {
		JsonObject page=new JsonObject();
		page.add("name",langs.get(locale).get("item.caupona."+name));
		page.addProperty("icon",new ResourceLocation(Main.MODID,name).toString());
		page.addProperty("category","caupona:recipes");
		CookingRecipe r=recipes.get(name);
		Item baseType=CPItems.any;
		if(r.getBase()!=null&&!r.getBase().isEmpty()) {
			StewBaseCondition sbc=r.getBase().get(0);
			if(sbc.equals(anyW))
				baseType=CPItems.anyWater;
			else if(sbc.equals(stock))
				baseType=CPItems.stock;
			else if(sbc.equals(milk))
				baseType=CPItems.milk;
		}
		JsonArray pages=new JsonArray();
		JsonObject imgpage=new JsonObject();
		imgpage.addProperty("type","caupona:thprecipe");
		imgpage.addProperty("img",new ResourceLocation(Main.MODID,"textures/gui/recipes/"+name+".png").toString());
		imgpage.addProperty("result",new ResourceLocation(Main.MODID,name).toString());
		imgpage.addProperty("base",baseType.getRegistryName().toString());
		pages.add(imgpage);
		page.add("pages", pages);
		return page;
	}
	private void saveEntry(String name,String locale,HashCache cache,JsonObject entry) {
		saveJson(cache,entry,bookmain.resolve(locale+"/entries/recipes/"+name+".json"));
	}
	private static void saveJson(HashCache cache, JsonObject recipeJson, Path path) {
		try {
			String s = GSON.toJson(recipeJson);
			String s1 = SHA1.hashUnencodedChars(s).toString();
			if (!Objects.equals(cache.getHash(path), s1) || !Files.exists(path)) {
				Files.createDirectories(path.getParent());

				try (BufferedWriter bufferedwriter = Files.newBufferedWriter(path)) {
					bufferedwriter.write(s);
				}
			}

			cache.putNew(path, s1);
		} catch (IOException ioexception) {
			LOGGER.error("Couldn't save data json {}", path, ioexception);
		}

	}
}
