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
	private static CachedDataDeserializer<CookIngredients,JsonElement> numbers=new CachedDataDeserializer<>();
	public static final Codec<CookIngredients> CODEC=Codec.STRING.dispatch("type", t->t.getType(), t->numbers.getCodec(t));;
	static {
		register("add", Add.CODEC, Add::new);
		register("ingredient", ItemIngredient.CODEC, ItemIngredient::new);
		register("item", ItemType.CODEC, ItemType::new);
		register("tag", ItemTag.CODEC, ItemTag::new);
		register("nop", NopNumber.CODEC, NopNumber::of);
		register("const", ConstNumber.CODEC, ConstNumber::new);
	}
	private Numbers(){
		
	}
	public static void register(String name, Deserializer<JsonElement, CookIngredients> des) {
		numbers.register(name, des);
	}
	public static void register(String name, Codec<? extends CookIngredients> rjson,
			Function<FriendlyByteBuf, CookIngredients> rpacket) {
		numbers.register(name, rjson, rpacket);
	}
	public static CookIngredients of(FriendlyByteBuf buffer) {
		return numbers.of(buffer);
	}
	public static void write(CookIngredients e, FriendlyByteBuf buffer) {
		buffer.writeUtf(e.getType());
		e.write(buffer);
	}
	public static void clearCache() {
		numbers.clearCache();
	}


}
