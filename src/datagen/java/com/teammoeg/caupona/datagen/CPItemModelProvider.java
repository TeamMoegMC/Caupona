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

package com.teammoeg.caupona.datagen;

import java.util.function.BiConsumer;

import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPItems;
import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.util.FoodMaterialInfo;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.item.ItemModel.Unbaked;
import net.minecraft.client.renderer.item.properties.select.ComponentContents;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;


public class CPItemModelProvider extends ItemModelGenerators {

	public CPItemModelProvider(ItemModelOutput itemModelOutput, BiConsumer<Identifier, ModelInstance> modelOutput) {
		super(itemModelOutput, modelOutput);
	}

	@Override
	public void run() {
		
		for (String s : CPItems.soups) {
			simpleTexture(s, "soups/");
			
		}
		for(String s:CPItems.bread_bowls)
			texture(s+"_loaf", "bread_bowls/"+s);
		for(String s:CPItems.dishes)
			texture(s+"_loaf", "bread_bowls/"+s);
		for (String s : CPItems.base_material)
			texture(s);
		for (FoodMaterialInfo s : CPItems.food_material)
			texture(s.name);
		simpleTexture("water", "soups/");
		simpleTexture("milk", "soups/");
		texture("loaf_bowl", "bread_bowl");
		texture("loaf", "cob_loaf");
		texture("loaf_dough");
		for (String s : CPItems.aspics)
			simpleTexture(s, "aspics/");
		simpleTexture("milk_based", "bases/");
		simpleTexture("stock_based", "bases/");
		simpleTexture("any_based", "bases/");
		simpleTexture("water_or_stock_based", "bases/");
		texture("book", "vade_mecum_for_innkeepers");
		texture(CPItems.clay_pot.get(), "clay_stew_pot");
		//texture("culinary_heat_haze");
		texture("soot");
		texture("portable_brazier");
		texture("walnut_boat");
		texture("chronoconis");
		texture("situla");
		texture("snail_block","snail_roe");
		texture("redstone_ladle");
		texture("bamboo_skimmer");
		texture("iron_skimmer");
		texture("scraps");
		texture("walnut_door");
		texture("walnut_sign");
		//itemModel(CPBlocks.SILPHIUM.get().asItem(),"silphium").transforms().transform(ItemDisplayContext.GUI).scale(0.5f).rotation(0, 45, 0).translation(0, -4, 0).end().end();
		/*System.out.println(new File("").getAbsolutePath());
		try {
			new BufferedReader(new FileReader(new File("../src/datagen/resources/assets/caupona/block/blocks.txt"))).lines().forEach( s -> {
				if(!ForgeRegistries.BLOCKS.containsKey(Identifier.fromNamespaceAndPath(CPMain.MODID,s.substring(0,s.lastIndexOf("."))))) {
					System.out.println(s);
				}
			});
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		/*super.singleTexture("walnut_sapling", Identifier.fromNamespaceAndPath("minecraft", "item/generated"), "layer0",
				Identifier.fromNamespaceAndPath(CPMain.MODID, "block/walnut_sapling"));
		super.singleTexture("fig_sapling", Identifier.fromNamespaceAndPath("minecraft", "item/generated"), "layer0",
				Identifier.fromNamespaceAndPath(CPMain.MODID, "block/fig_sapling"));
		super.singleTexture("wolfberry_sapling", Identifier.fromNamespaceAndPath("minecraft", "item/generated"), "layer0",
				Identifier.fromNamespaceAndPath(CPMain.MODID, "block/wolfberry_sapling"));*/
		// super.withExistingParent("clay_cistern",new
		// Identifier(Main.MODID,"block/clay_cistern"));
		for (String s : CPItems.spices)
			simpleTexture(s, "");
		for (String s : CPItems.dishes) {
			simpleTexture(s, "sauteed_dishes/");
			//simpleTexture(s+"_loaf", "bread_bowls/");
		}
		this.itemModelOutput.accept(CPItems.gravy_boat.get(),
		ItemModelUtils.select(new ComponentContents<>(DataComponents.DAMAGE),plain("oil_bottle"),
			ItemModelUtils.when(4, plain("walnut_oil_4")),
			ItemModelUtils.when(3, plain("walnut_oil_3")),
			ItemModelUtils.when(2, plain("walnut_oil_2")),
			ItemModelUtils.when(1, plain("walnut_oil_1")),
			ItemModelUtils.when(0, plain("walnut_oil_0"))));
		/*
		texture("gravy_boat", "walnut_oil_0").override().predicate(Identifier.withDefaultNamespace("damaged"), 1)
				.predicate(Identifier.withDefaultNamespace("damage"), 0.2f).model(texture("")).end().override()
				.predicate(Identifier.withDefaultNamespace("damaged"), 1).predicate(Identifier.withDefaultNamespace("damage"), 0.4f)
				.model(texture("")).end().override().predicate(Identifier.withDefaultNamespace("damaged"), 1)
				.predicate(Identifier.withDefaultNamespace("damage"), 0.6f).model(texture("")).end().override()
				.predicate(Identifier.withDefaultNamespace("damaged"), 1).predicate(Identifier.withDefaultNamespace("damage"), 0.8f)
				.model(texture("")).end().override().predicate(Identifier.withDefaultNamespace("damaged"), 1)
				.predicate(Identifier.withDefaultNamespace("damage"), 1f).model(texture("oil_bottle")).end();*/
	}

	public void simpleTexture(String name, String par) {
		this.itemModelOutput.accept(BuiltInRegistries.ITEM.getValue(CPMain.rl(name)),
		ItemModelUtils.plainModel(ModelTemplates.FLAT_ITEM.create(CPMain.rl("item/" + name),new TextureMapping().put(TextureSlot.LAYER0, new Material(CPMain.rl("item/" + par + name),false)), this.modelOutput))
		);

	}
	public Unbaked plain(String name) {
		return plain(name,"");
	}
	public Unbaked plain(String name, String par) {
		return ItemModelUtils.plainModel(ModelTemplates.FLAT_ITEM.create(CPMain.rl("item/" + name),new TextureMapping().put(TextureSlot.LAYER0, new Material(CPMain.rl("item/" + par + name),false)), this.modelOutput))
		;

	}
	public void texture(String name) {
		texture(name, name);
	}
	public void texture(Item name, String par) {
		this.itemModelOutput.accept(name,
			ItemModelUtils.plainModel(ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(name), TextureMapping.layer0(new Material(Identifier.fromNamespaceAndPath(CPMain.MODID, "item/"+par))), this.modelOutput)
				));
	}
	public void texture(String name, String par) {
		texture(BuiltInRegistries.ITEM.getValue(CPMain.rl(name)),par);
	}
}
