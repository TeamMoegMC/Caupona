package com.teammoeg.caupona.data.recipes.conditions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import com.google.gson.JsonObject;
import com.teammoeg.caupona.data.CachedDataDeserializer;
import com.teammoeg.caupona.data.Deserializer;
import com.teammoeg.caupona.data.InvalidRecipeException;
import com.teammoeg.caupona.data.recipes.IngredientCondition;

import net.minecraft.network.FriendlyByteBuf;

public class Conditions {
	private static CachedDataDeserializer<IngredientCondition,JsonObject> numbers=new CachedDataDeserializer<>() {

		@Override
		protected IngredientCondition internalOf(JsonObject json) {
			return getDeserializer(json.get("cond").getAsString()).read(json);
		}
		
	};
	static {
		register("half", Halfs::new, Halfs::new);
		register("mainly", Mainly::new, Mainly::new);
		register("contains", Must::new, Must::new);
		register("mainlyOf", MainlyOfType::new, MainlyOfType::new);
		register("only", Only::new, Only::new);
	}
	public static void register(String name, Deserializer<JsonObject, IngredientCondition> des) {
		numbers.register(name, des);
	}

	public static void register(String name, Function<JsonObject, IngredientCondition> rjson,
			Function<FriendlyByteBuf, IngredientCondition> rpacket) {
		numbers.register(name, rjson, rpacket);
	}

	public static IngredientCondition of(JsonObject jsonElement) {
		return numbers.of(jsonElement);
	}

	public static IngredientCondition of(FriendlyByteBuf buffer) {
		return numbers.of(buffer);
	}

	public static void checkConditions(Collection<IngredientCondition> allow) {
		if(allow==null)return;
		boolean foundMajor=false;
		Set<Class<? extends IngredientCondition>> conts=new HashSet<>();
		
		for(IngredientCondition c:allow) {
			if(c.isMajor()) {
				if(foundMajor)
					throw new InvalidRecipeException("There must be less than one major condition. (Current: "+c.getType()+")");
				foundMajor=true;
			}else if(c.isExclusive()) {
				if(conts.contains(c.getClass()))
					throw new InvalidRecipeException("There must be less than one "+c.getType()+" condition.");
				conts.add(c.getClass());
			}
		}
	}

	public static void write(IngredientCondition e, FriendlyByteBuf buffer) {
		buffer.writeUtf(e.getType());
		e.write(buffer);
	}

	public static void clearCache() {
		numbers.clearCache();
	}
}
