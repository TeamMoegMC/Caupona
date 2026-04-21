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

package com.teammoeg.caupona.util;

import java.util.HashMap;
import java.util.Map;

import com.teammoeg.caupona.CPMain;

import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.transfer.item.ItemResource;

public record FuelType (TagKey<Item> it,Identifier modelLayer,Identifier cold_ash,Identifier hot_ash){
	private static final Map<Identifier,FuelType> types=new HashMap<>();
	public static final FuelType WOODS=register(new FuelType("fuel/woods","firewoods","cold_ash","hot_ash"));
	public static final FuelType CHARCOAL=register(new FuelType("fuel/charcoals","charcoal","cold_ash","hot_ash"));
	public static final FuelType FOSSIL=register(new FuelType("fuel/fossil","coal","cold_ash","hot_ash"));
	public static final FuelType LAVA=register(new FuelType("fuel/lava","lava_bucket","cooled_lava","lava"));
	public static final FuelType OTHER=register(new FuelType(ItemTags.create(CPMain.rl("fuel/others")),null,null,null));
	


	public static final FuelType register(FuelType type) {
		//FuelType orig=types.get(type.it.location());
		types.put(type.it.location(), type);
		return type;
	}


	private FuelType(String tagname,String modelLayer, String hot_ash, String cold_ash) {
		this(CPMain.rl(tagname),modelLayer,hot_ash,cold_ash);
	}
	private FuelType(Identifier tag,String modelLayer, String hot_ash, String cold_ash) {
		this(ItemTags.create(tag),CPMain.rl("block/dynamic/kitchen_stove_fuels/kitchen_stove_"+modelLayer),CPMain.rl("block/dynamic/kitchen_stove_fuels/kitchen_stove_"+hot_ash),CPMain.rl("block/dynamic/kitchen_stove_fuels/kitchen_stove_"+cold_ash));
	}

	public static FuelType getType(ItemResource is) {
		if(is.isEmpty())
			return FuelType.OTHER;
		for (FuelType ft : types.values()) {
			if (is.is(ft.it))
				return ft;
		}
		return FuelType.OTHER;
	}
	public static FuelType parse(String toParse) {
		return types.getOrDefault(Identifier.parse(toParse),FuelType.OTHER);
	}
	public String serialize() {
		return it.location().toString();
	}


	public TagKey<Item> it() {
		return it;
	}


	public Identifier modelLayer() {
		return modelLayer;
	}


	public Identifier cold_ash() {
		return cold_ash;
	}


	public Identifier hot_ash() {
		return hot_ash;
	}
}
