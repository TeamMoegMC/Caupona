package com.teammoeg.caupona.data;

import java.util.function.Function;

import com.mojang.serialization.Codec;

import net.minecraft.network.FriendlyByteBuf;

public class Deserializer<U extends Writeable> {
	private int id;
	public Codec<U> fromJson;
	public Function<FriendlyByteBuf, U> fromPacket;

	public Deserializer(Codec<U> fromJson, Function<FriendlyByteBuf, U> fromPacket,int id) {
		super();
		this.fromJson = fromJson;
		this.fromPacket = fromPacket;
		this.id=id;
	}

	public U read(FriendlyByteBuf packet) {
		return fromPacket.apply(packet);
	}

	public void write(FriendlyByteBuf packet, U obj) {
		packet.writeByte(id);
		obj.write(packet);
	}
}