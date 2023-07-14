package com.teammoeg.caupona.data.recipes.numbers;

import java.util.function.Function;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.teammoeg.caupona.data.CachedDataDeserializer;
import com.teammoeg.caupona.data.Deserializer;
import com.teammoeg.caupona.data.recipes.CookIngredients;

import net.minecraft.network.FriendlyByteBuf;

public class Numbers{
	private static CachedDataDeserializer<CookIngredients,JsonElement> numbers=new CachedDataDeserializer<>(){
		@Override
		protected CookIngredients internalOf(JsonElement jsonElement) {
			if (jsonElement == null || jsonElement.isJsonNull())
				return NopNumber.INSTANCE;
			if (jsonElement.isJsonPrimitive()) {
				JsonPrimitive jp = jsonElement.getAsJsonPrimitive();
				if (jp.isString())
					return new ItemTag(jp);
				else if (jp.isNumber())
					return new ConstNumber(jp);
			}
			if (jsonElement.isJsonArray())
				return new Add(jsonElement);
			JsonObject jo = jsonElement.getAsJsonObject();
			if (jo.has("type")) {
				Deserializer<JsonElement, CookIngredients> factory = getDeserializer(jo.get("type").getAsString());
				if (factory == null)
					return NopNumber.INSTANCE;
				return factory.read(jo);
			}
			if (jo.has("item"))
				return new ItemType(jo);
			else if (jo.has("ingredient"))
				return new ItemIngredient(jo);
			else if (jo.has("types"))
				return new Add(jo);
			else if (jo.has("tag"))
				return new ItemTag(jo);
			return NopNumber.INSTANCE;
		}
	};
	static {
		register("add", Add::new, Add::new);
		register("ingredient", ItemIngredient::new, ItemIngredient::new);
		register("item", ItemType::new, ItemType::new);
		register("tag", ItemTag::new, ItemTag::new);
		register("nop", NopNumber::of, NopNumber::of);
		register("const", ConstNumber::new, ConstNumber::new);
	}
	private Numbers(){
		
	}
	public static void register(String name, Deserializer<JsonElement, CookIngredients> des) {
		numbers.register(name, des);
	}
	public static void register(String name, Function<JsonElement, CookIngredients> rjson,
			Function<FriendlyByteBuf, CookIngredients> rpacket) {
		numbers.register(name, rjson, rpacket);
	}
	public static CookIngredients of(JsonElement jsonElement) {
		return numbers.of(jsonElement);
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
