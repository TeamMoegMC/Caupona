/*
 * Copyright (c) 2022 TeamMoeg
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

package com.teammoeg.caupona.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.teammoeg.caupona.util.DataOps;

import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * Tool class for serialize data, packets etc
 */
public class SerializeUtil {
	

	private SerializeUtil() {

	}

	public static <T> Optional<T> readOptional(FriendlyByteBuf buffer, Function<FriendlyByteBuf, T> func) {
		if (buffer.readBoolean())
			return Optional.ofNullable(func.apply(buffer));
		return Optional.empty();
	}

	public static <T> void writeOptional2(FriendlyByteBuf buffer, T data, BiConsumer<FriendlyByteBuf, T> func) {
		writeOptional(buffer, data, (a, b) -> func.accept(b, a));
	}

	public static <T> void writeOptional(FriendlyByteBuf buffer, T data, BiConsumer<T, FriendlyByteBuf> func) {
		writeOptional(buffer, Optional.ofNullable(data), func);
	}

	public static <T> void writeOptional(FriendlyByteBuf buffer, Optional<T> data,
			BiConsumer<T, FriendlyByteBuf> func) {
		if (data.isPresent()) {
			buffer.writeBoolean(true);
			func.accept(data.get(), buffer);
			return;
		}
		buffer.writeBoolean(false);
	}

	public static <T> List<T> readList(FriendlyByteBuf buffer, Function<FriendlyByteBuf, T> func) {
		if (!buffer.readBoolean())
			return null;
		int cnt = buffer.readVarInt();
		List<T> nums = new ArrayList<>(cnt);
		for (int i = 0; i < cnt; i++)
			nums.add(func.apply(buffer));
		return nums;
	}

	public static <T> void writeList(FriendlyByteBuf buffer, Collection<T> elms, BiConsumer<T, FriendlyByteBuf> func) {
		if (elms == null) {
			buffer.writeBoolean(false);
			return;
		}
		buffer.writeBoolean(true);
		buffer.writeVarInt(elms.size());
		elms.forEach(e -> func.accept(e, buffer));
	}

	public static <T> void writeList2(FriendlyByteBuf buffer, Collection<T> elms, BiConsumer<FriendlyByteBuf, T> func) {
		if (elms == null) {
			buffer.writeBoolean(false);
			return;
		}
		buffer.writeBoolean(true);
		buffer.writeVarInt(elms.size());
		elms.forEach(e -> func.accept(buffer, e));
	}

	public static <T> List<T> parseJsonList(JsonElement elm, Function<JsonObject, T> mapper) {
		if (elm == null)
			return Lists.newArrayList();
		if (elm.isJsonArray())
			return StreamSupport.stream(elm.getAsJsonArray().spliterator(), false).map(JsonElement::getAsJsonObject)
					.map(mapper).collect(Collectors.toList());
		return Lists.newArrayList(mapper.apply(elm.getAsJsonObject()));
	}

	public static <T> List<T> parseJsonElmList(JsonElement elm, Function<JsonElement, T> mapper) {
		if (elm == null)
			return Lists.newArrayList();
		if (elm.isJsonArray())
			return StreamSupport.stream(elm.getAsJsonArray().spliterator(), false).map(mapper)
					.collect(Collectors.toList());
		return Lists.newArrayList(mapper.apply(elm.getAsJsonObject()));
	}

	public static <T> JsonArray toJsonList(Collection<T> li, Function<T, JsonElement> mapper) {
		JsonArray ja = new JsonArray();
		li.stream().map(mapper).forEach(ja::add);
		return ja;
	}

	public static <T> ListTag toNBTList(Collection<T> stacks, Function<T, Tag> mapper) {
		ListTag nbt = new ListTag();
		stacks.stream().map(mapper).forEach(nbt::add);
		return nbt;
	}
	public static FluidStack readFluidStack(JsonElement jsonIn) {
		if(jsonIn==null)return null;
		return FluidStack.CODEC.decode(JsonOps.INSTANCE,jsonIn).result().map(Pair::getFirst).orElse(FluidStack.EMPTY);
	}
	public static FluidStack readFluidStack(FriendlyByteBuf in) {
		Fluid f=in.readById(BuiltInRegistries.FLUID);
		int amount=in.readVarInt();
		FluidStack fs=new FluidStack(f,amount);
		readOptional(in,d->d.readNbt()).ifPresent(e->fs.setTag(e));
		return fs;
	}
	public static JsonElement writeFluidStack(FluidStack stack) {
		return FluidStack.CODEC.encodeStart(JsonOps.INSTANCE,stack).result().orElse(null);
	}
	public static void writeFluidStack(FriendlyByteBuf out,FluidStack stack) {
		out.writeId(BuiltInRegistries.FLUID,stack.getFluid());
		out.writeVarInt(stack.getAmount());
		writeOptional(out,stack.getTag(),(s,d)->d.writeNbt(s));
	}
    public static <K, V> void writeMap(FriendlyByteBuf buffer, Map<K, V> elms, BiConsumer<K, FriendlyByteBuf> keywriter, BiConsumer<V, FriendlyByteBuf> valuewriter) {
        writeList(buffer, elms.entrySet(), (p, b) -> {
            keywriter.accept(p.getKey(), b);
            valuewriter.accept(p.getValue(), b);
        });
    }
    public static <K, V> Map<K, V> readMap(FriendlyByteBuf buffer, Map<K, V> map, Function<FriendlyByteBuf, K> keyreader, Function<FriendlyByteBuf, V> valuereader) {
        map.clear();
        if (!buffer.readBoolean())
            return map;
        int cnt = buffer.readVarInt();
        for (int i = 0; i < cnt; i++)
            map.put(keyreader.apply(buffer), valuereader.apply(buffer));
        return map;
    }
    public static <T> void writeCodec(FriendlyByteBuf pb, Codec<T> codec, T obj) {
    	DataResult<Object> ob=codec.encodeStart(DataOps.COMPRESSED, obj);
    	Optional<Object> ret=ob.resultOrPartial(EncoderException::new);
    	writeObject(pb,ret.get());
    }
    public static <T> T readCodec(FriendlyByteBuf pb, Codec<T> codec) {
    	DataResult<Pair<T, Object>> ob=codec.decode(DataOps.COMPRESSED, readObject(pb));
    	Optional<Pair<T, Object>> ret=ob.resultOrPartial(DecoderException::new);
    	return ret.get().getFirst();
    }
    public static void writeObject(FriendlyByteBuf pb,Object input) {
		if(input instanceof Byte) {
			pb.writeByte(1);
			pb.writeByte((Byte)input);
		}else if(input instanceof Short) {
			pb.writeByte(2);
			pb.writeShort((Short) input);
		}else if(input instanceof Integer) {
			pb.writeByte(3);
			pb.writeVarInt((Integer) input);
		}else if(input instanceof Long) {
			pb.writeByte(4);
			pb.writeLong((Long) input);
		}else if(input instanceof Float) {
			pb.writeByte(5);
			pb.writeFloat((Float) input);
		}else if(input instanceof Double) {
			pb.writeByte(6);
			pb.writeDouble((Double) input);
		}else if(input instanceof String) {
			pb.writeByte(7);
			pb.writeUtf((String) input);
		}else if(input instanceof Map) {
			pb.writeByte(8);
			SerializeUtil.writeList(pb, ((Map<Object,Object>)input).entrySet(),(t,p)->{writeObject(pb,t.getKey());writeObject(pb,t.getValue());});
		}else if(input instanceof List) {
			Class<?> cls=DataOps.getElmClass(((List<Object>)input));
			if(cls==Byte.class) {
				pb.writeByte(9);
				byte[] bs=DataOps.INSTANCE.getByteArray(input).result().get();
				pb.writeVarInt(bs.length);
				pb.writeBytes(bs);
			}else if(cls==Integer.class) {
				pb.writeByte(10);
				SerializeUtil.writeList(pb, ((List<Integer>)input), (t,p)->p.writeVarInt(t));
			}else if(cls==Long.class) {
				pb.writeByte(11);
				SerializeUtil.writeList(pb, ((List<Long>)input), (t,p)->p.writeLong(t));
			}else {
				pb.writeByte(12);
				SerializeUtil.writeList2(pb, ((List<Object>)input), SerializeUtil::writeObject);
			}
		}
		pb.writeByte(0);
    }
    public static Object readObject(FriendlyByteBuf pb) {
    	switch(pb.readByte()) {
    	case 1:return pb.readByte();
    	case 2:return pb.readShort();
    	case 3:return pb.readVarInt();
    	case 4:return pb.readLong();
    	case 5:return pb.readFloat();
    	case 6:return pb.readDouble();
    	case 7:return pb.readUtf();
    	case 8:return SerializeUtil.readMap(pb, new HashMap<>(), SerializeUtil::readObject, SerializeUtil::readObject);
    	case 9:return pb.readBytes(pb.readVarInt());
    	case 10:return SerializeUtil.readList(pb, FriendlyByteBuf::readVarInt);
    	case 11:return SerializeUtil.readList(pb, FriendlyByteBuf::readLong);
    	case 12:return SerializeUtil.readList(pb, SerializeUtil::readObject);
    	}
    	return null;
    }
	
}
