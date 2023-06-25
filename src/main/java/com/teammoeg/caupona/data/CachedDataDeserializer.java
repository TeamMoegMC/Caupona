package com.teammoeg.caupona.data;

import java.util.HashMap;
import java.util.function.Function;

import com.google.gson.JsonElement;
import com.teammoeg.caupona.util.CacheMap;

import net.minecraft.network.FriendlyByteBuf;

public abstract class CachedDataDeserializer<T extends Writeable,U extends JsonElement> {
	private HashMap<String, Deserializer<U,T>> deserializers = new HashMap<>();
	private CacheMap<T> cache = new CacheMap<>();
	public void register(String name, Deserializer<U,T> des) {
		deserializers.put(name, des);
	}
	public void register(String name, Function<U,T> rjson,
			Function<FriendlyByteBuf, T> rpacket) {
		register(name, new Deserializer<>(rjson, rpacket));
	}
	public T of(U jsonElement) {
		return cache.of(internalOf(jsonElement));
	}
	public Deserializer<U,T> getDeserializer(String type){
		return deserializers.get(type);
	}
	protected abstract T internalOf(U jsonElement);
	public T of(FriendlyByteBuf buffer) {
		return cache.of(getDeserializer(buffer.readUtf()).read(buffer));
	}
	public void clearCache() {
		cache.clear();
	}
	
}
