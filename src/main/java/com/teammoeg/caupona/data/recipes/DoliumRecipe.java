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

package com.teammoeg.caupona.data.recipes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.api.CauponaHooks;
import com.teammoeg.caupona.components.IFoodInfo;
import com.teammoeg.caupona.components.StewInfo;
import com.teammoeg.caupona.data.IDataRecipe;
import com.teammoeg.caupona.util.RecipeHandleStatus;
import com.teammoeg.caupona.util.SizedOrCatalystFluidIngredient;
import com.teammoeg.caupona.util.SizedOrCatalystIngredient;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class DoliumRecipe extends IDataRecipe{
	public static List<RecipeHolder<DoliumRecipe>> recipes;
	public static Map<Identifier,RecipeHolder<DoliumRecipe>> recipesNames;
	public static DeferredHolder<RecipeType<?>,RecipeType<DoliumRecipe>> TYPE;
	public static DeferredHolder<RecipeSerializer<?>,RecipeSerializer<DoliumRecipe>> SERIALIZER;

	@Override
	public RecipeSerializer<DoliumRecipe> getSerializer() {
		return SERIALIZER.get();
	}

	@Override
	public RecipeType<DoliumRecipe> getType() {
		return TYPE.get();
	}

	public List<SizedOrCatalystIngredient> items;
	public Ingredient extra;
	public Fluid base;
	public SizedOrCatalystFluidIngredient fluid ;
	public float density = 0;
	public boolean keepInfo = false;
	public ItemStackTemplate output;
	public int time;
	public static final MapCodec<DoliumRecipe> CODEC=
			RecordCodecBuilder.mapCodec(t->t.group(
					Codec.list(SizedOrCatalystIngredient.NESTED_CODEC).fieldOf("items").forGetter(o->o.items),
					Ingredient.CODEC.optionalFieldOf("container").forGetter(o->Optional.ofNullable(o.extra)),
					BuiltInRegistries.FLUID.byNameCodec().optionalFieldOf("base").forGetter(o->Optional.ofNullable(o.base)),
					SizedOrCatalystFluidIngredient.NESTED_CODEC.optionalFieldOf("fluid").forGetter(o->Optional.ofNullable(o.fluid)),
					Codec.FLOAT.fieldOf("density").forGetter(o->o.density),
					Codec.BOOL.fieldOf("keepInfo").forGetter(o->o.keepInfo),
					ItemStackTemplate.CODEC.fieldOf("output").forGetter(o->o.output),
					Codec.INT.optionalFieldOf("time", 1200).forGetter(o->o.time)
					).apply(t, DoliumRecipe::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, DoliumRecipe> STREAM_CODEC=StreamCodec.composite(
			SizedOrCatalystIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()),o->o.items,
			Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC,o->Optional.ofNullable(o.extra),
			ByteBufCodecs.optional(ByteBufCodecs.registry(Registries.FLUID)),o->Optional.ofNullable(o.base),
			ByteBufCodecs.optional(SizedOrCatalystFluidIngredient.STREAM_CODEC),o->Optional.ofNullable(o.fluid),
			ByteBufCodecs.FLOAT,o->o.density,
			ByteBufCodecs.BOOL,o->o.keepInfo,
			ItemStackTemplate.STREAM_CODEC,o->o.output,
			ByteBufCodecs.VAR_INT,o->o.time,
			
			DoliumRecipe::new
			);
	public DoliumRecipe(Fluid base, Fluid fluid, int amount, float density,
			boolean keep, ItemStackTemplate out, List<SizedOrCatalystIngredient> items, int time) {
		this( base, fluid, amount, density, keep, out, items, null,time);
	}

	public DoliumRecipe(List<SizedOrCatalystIngredient> items, Optional<Ingredient> extra, Optional<Fluid> base, Optional<SizedOrCatalystFluidIngredient> fluid,
			 float density, boolean keepInfo, ItemStackTemplate output,int time) {
		super();
		this.items = items;
		this.extra = extra.orElse(null);
		this.base = base.orElse(null);
		this.fluid = fluid.orElse(null);
		this.density = density;
		this.keepInfo = keepInfo;
		this.output = output;
		this.time=time;
	}

	public DoliumRecipe(Fluid base, Fluid fluid, int amount, float density,
			boolean keep, ItemStackTemplate out, Collection<SizedOrCatalystIngredient> items, Ingredient ext,int time) {
		if (items != null)
			this.items = new ArrayList<>(items);
		else
			this.items = new ArrayList<>();

		this.base = base;
		if(fluid!=Fluids.EMPTY)
			this.fluid = SizedOrCatalystFluidIngredient.of(fluid,amount);
		this.density = density;
		this.output = out;
		this.extra = ext;
		keepInfo = keep;
		this.time=time;
	}

	public static DoliumRecipe testPot(FluidStack fluidStack) {
		return recipes.stream().map(t->t.value()).filter(t -> t.test(fluidStack, ItemStack.EMPTY)).findFirst().orElse(null);
	}

	public static boolean testInput(ItemStack stack) {
		return recipes.stream().map(t->t.value()).anyMatch(t -> t.items.stream().anyMatch(i -> i.test(stack)));
	}

	public static boolean testContainer(ItemStack stack) {
		return recipes.stream().map(t->t.value()).map(t -> t.extra).filter(Objects::nonNull).anyMatch(t -> t.test(stack));
	}

	public static RecipeHolder<DoliumRecipe> testDolium(ResourceHandler<FluidResource> f, ResourceHandler<ItemResource> inv,@Nullable Identifier id) {
		ItemStack is0 = inv.getResource(0).toStack();
		ItemStack is1 = inv.getResource(1).toStack();
		ItemStack is2 = inv.getResource(2).toStack();
		ItemStack cont = inv.getResource(4).toStack();
		FluidStack fs=FluidUtil.getStack(f, 0);
		if(id!=null) {
			RecipeHolder<DoliumRecipe> recipe=recipesNames.get(id);
			if(recipe.value().test(fs, cont, is0,is1,is2))
				return recipe;
		}else
			return recipes.stream().filter(t -> t.value().test(fs, cont, is0, is1, is2)).findFirst().orElse(null);
		return null;
	}

	public boolean test(FluidStack f, ItemStack container, ItemStack... ss) {
		if (items.size() > 0) {
			if (ss.length < items.size())
				return false;
			int notEmpty = 0;
			for (ItemStack is : ss)
				if (!is.isEmpty())
					notEmpty++;
			if (notEmpty < items.size())
				return false;
		}
		if (extra != null && !extra.test(container))
			return false;
		if(fluid!=null&&!fluid.test(f))
			return false;

		if (density != 0 || base != null) {
			Optional<IFoodInfo> opinfo = CauponaHooks.getInfo(f);
			if(opinfo.isEmpty())
				return false;
			IFoodInfo info=opinfo.get();
			if (base != null && base!=info.getBase())
				return false;
			if (info.getDensity() < density)
				return false;
		}
		for (SizedOrCatalystIngredient igd : items) {
			boolean flag = false;
			for (ItemStack is : ss) {
				if (igd.test(is) ) {
					flag = true;
					break;
				}
			}
			if (!flag)
				return false;
		}
		return true;
	}

	public RecipeHandleStatus handle(ResourceHandler<FluidResource> f,ResourceHandler<ItemResource> inv,int outSlot) {
		try(Transaction child=Transaction.openRoot()){
			int times = 1;
			if (fluid.amount() > 0)
				times = f.getAmountAsInt(0) / fluid.amount();
			times=Math.min(times, (output.count()+inv.getAmountAsInt(outSlot))/inv.getCapacityAsInt(outSlot, ItemResource.of(output)));
			
			ItemStack out = output.create();
			FluidResource fs=f.getResource(0);
			if (keepInfo) {
				StewInfo info = Utils.getOrCreateInfoForRead(fs);
				Utils.setInfo(out, info);
			}
			if(f.extract(fs, times * fluid.amount(), child)==times * fluid.amount()) {
				if(inv.insert(ItemResource.of(out), output.count() * times, child)==output.count() * times) {
					child.commit();
					return RecipeHandleStatus.SUCCEED;
				}else {
					return RecipeHandleStatus.BLOCKED;
				}
			}
		}
		return RecipeHandleStatus.FAILED;
	}

	public RecipeHandleStatus handleDolium(ResourceHandler<FluidResource> f,ResourceHandler<ItemResource> inv) {
		try(Transaction child=Transaction.openRoot()){
			int times = output.getMaxStackSize();
			if (fluid!=null&&fluid.amount() > 0)
				times = Math.min(f.getAmountAsInt(0) / fluid.amount(), times);
			if (extra != null)
				times = Math.min(times, inv.getAmountAsInt(4));
			times=Math.min(times, (output.count()+inv.getAmountAsInt(5))/inv.getCapacityAsInt(5, ItemResource.of(output)));
			for (SizedOrCatalystIngredient igd : items) {
				if (igd.count() == 0)
					continue;
				int remain=igd.count();
				for(int i=0;i<3;i++) {
					ItemResource rs=inv.getResource(i);
					if(igd.test(rs.toStack())) {
						remain-=inv.extract(i,rs, remain, child);
					}
				}
				if(remain>0)
					return RecipeHandleStatus.FAILED;
			}
	
			if (extra != null) {
				ItemResource cont=inv.getResource(4);
				if(inv.extract(4, cont, times, child)!=times)
					return RecipeHandleStatus.FAILED;
			}
			FluidResource fr=f.getResource(0);
			ItemStack out = output.create();
			if (keepInfo) {
				StewInfo info = Utils.getOrCreateInfoForRead(fr);
				Utils.setInfo(out, info);
			}
			if (fluid!=null&&fluid.amount() > 0)
				if(f.extract(fr, times * fluid.amount(), child)==times * fluid.amount()) {
					if(inv.insert(5,ItemResource.of(out), output.count() * times, child)==output.count() * times) {
						child.commit();
						return RecipeHandleStatus.SUCCEED;
					}else {
						return RecipeHandleStatus.BLOCKED;
					}
				}
		}
		return RecipeHandleStatus.FAILED;
	}
/*
	public DoliumRecipe(FriendlyByteBuf data) {
		items = SerializeUtil.readList(data, d -> Pair.of(Ingredient.fromNetwork(d), d.readVarInt()));
		base = SerializeUtil.readOptional(data, FriendlyByteBuf::readResourceLocation).orElse(null);
		fluid = data.readById(BuiltInRegistries.FLUID);
		amount = data.readVarInt();
		density = data.readFloat();
		keepInfo = data.readBoolean();
		output = data.readItem();
		extra = SerializeUtil.readOptional(data, Ingredient::fromNetwork).orElse(null);
	}

	public void write(FriendlyByteBuf data) {
		SerializeUtil.writeList(data, items, (r, d) -> {
			r.getFirst().toNetwork(data);
			data.writeVarInt(r.getSecond());
		});
		SerializeUtil.writeOptional2(data, base, FriendlyByteBuf::writeResourceLocation);
		data.writeId(BuiltInRegistries.FLUID, fluid);
		data.writeVarInt(amount);
		data.writeFloat(density);
		data.writeBoolean(keepInfo);
		data.writeItem(output);
		SerializeUtil.writeOptional(data, extra, Ingredient::toNetwork);
	}
*/

	public int getTime() {
		return time;
	}

}
