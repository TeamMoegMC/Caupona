package com.teammoeg.caupona.data.recipes.numbers;

import java.util.function.Function;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.teammoeg.caupona.data.CachedDataDeserializer;
import com.teammoeg.caupona.data.Deserializer;
import com.teammoeg.caupona.data.recipes.CookIngredients;
import com.teammoeg.caupona.data.recipes.StewBaseCondition;

import net.minecraft.network.FriendlyByteBuf;

public class Numbers{
	private static CachedDataDeserializer<CookIngredients> numbers=new CachedDataDeserializer<>();
	public static final Codec<CookIngredients> CODEC=numbers.createCodec();
	static {
		register("add", Add.class, Add.CODEC, Add::new);
		register("ingredient", ItemIngredient.class, ItemIngredient.CODEC, ItemIngredient::new);
		register("item", ItemType.class, ItemType.CODEC, ItemType::new);
		register("tag", ItemTag.class, ItemTag.CODEC, ItemTag::new);
		register("nop", NopNumber.class, NopNumber.CODEC, NopNumber::of);
		register("const", ConstNumber.class, ConstNumber.CODEC, ConstNumber::new);
	}
	private Numbers(){
		
	}
	public static <T extends CookIngredients> void register(String name,Class<T> cls, Codec<T> rjson,
			Function<FriendlyByteBuf, T> rpacket) {
		numbers.register(name, cls, rjson, rpacket);
	}
	public static CookIngredients of(FriendlyByteBuf buffer) {
		return numbers.of(buffer);
	}
	public static void write(CookIngredients e, FriendlyByteBuf buffer) {
		numbers.write(buffer, e);
	}
	public static void clearCache() {
		numbers.clearCache();
	}


}
