package com.teammoeg.caupona.data;

import java.util.function.Function;

import com.google.gson.JsonElement;

import net.minecraft.network.FriendlyByteBuf;

public class Deserializer<T extends JsonElement, U extends Writeable> {
	private int id;
	public Function<T, U> fromJson;
	public Function<FriendlyByteBuf, U> fromPacket;

	public Deserializer(Function<T, U> fromJson, Function<FriendlyByteBuf, U> fromPacket) {
		super();
		this.fromJson = fromJson;
		this.fromPacket = fromPacket;
	}

	public U read(T json) {
		return fromJson.apply(json);
	}

	public U read(FriendlyByteBuf packet) {
		return fromPacket.apply(packet);
	}

	public void write(FriendlyByteBuf packet, U obj) {
		packet.writeVarInt(id);
		obj.write(packet);
	}

	public JsonElement serialize(U obj) {
		return obj.serialize();
	}
}