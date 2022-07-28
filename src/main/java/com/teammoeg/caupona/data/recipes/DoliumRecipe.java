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
 * You should have received a copy of the GNU General Public License
 * along with Caupona. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.caupona.data.recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.teammoeg.caupona.data.IDataRecipe;
import com.teammoeg.caupona.data.InvalidRecipeException;
import com.teammoeg.caupona.data.SerializeUtil;
import com.teammoeg.caupona.fluid.SoupFluid;
import com.teammoeg.caupona.items.StewItem;
import com.teammoeg.caupona.util.SoupInfo;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.crafting.NBTIngredient;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DoliumRecipe extends IDataRecipe {
	public static List<DoliumRecipe> recipes;
	public static RecipeType<?> TYPE;
	public static RegistryObject<RecipeSerializer<?>> SERIALIZER;

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return TYPE;
	}

	public List<Pair<Ingredient,Integer>> items;
	Ingredient extra;
	public ResourceLocation base;
	public Fluid fluid=Fluids.EMPTY;
	public int amount=250;
	public float density=0;
	public boolean keepInfo=false;
	public ItemStack output;
	public DoliumRecipe(ResourceLocation id, ResourceLocation base, Fluid fluid, int amount,float density,boolean keep,ItemStack out,List<Pair<Ingredient,Integer>> items) {
		this(id, base, fluid, amount, density, keep, out, items,null);
	}
	public DoliumRecipe(ResourceLocation id, ResourceLocation base, Fluid fluid, int amount,float density,boolean keep,ItemStack out,List<Pair<Ingredient,Integer>> items,Ingredient ext) {
		super(id);
		if(items!=null)
			this.items = new ArrayList<>(items);
		else
			this.items=new ArrayList<>();
		
		this.base = base;
		this.fluid = fluid;
		this.density = density;
		this.amount=amount;
		this.output=out;
		this.extra=ext;
		keepInfo=keep;
	}
	
	public DoliumRecipe(ResourceLocation id, JsonObject jo) {
		super(id);
		if(jo.has("items")) 
			items=SerializeUtil.parseJsonList(jo.get("items"),j->Pair.of(Ingredient.fromJson(j.get("item")),(j.has("count")?j.get("count").getAsInt():1)));
		
		if(jo.has("base"))
			base=new ResourceLocation(jo.get("base").getAsString());
		if(jo.has("fluid"))
			fluid=ForgeRegistries.FLUIDS.getValue(new ResourceLocation(jo.get("fluid").getAsString()));
		if(jo.has("amount"))
			amount=jo.get("amount").getAsInt();
		if(jo.has("density"))
			density=jo.get("density").getAsFloat();
		if(jo.has("keepInfo"))
			keepInfo=jo.get("keepInfo").getAsBoolean();
		output=Ingredient.fromJson(jo.get("output")).getItems()[0];
		if(jo.has("container"))
			extra=Ingredient.fromJson(jo.get("container"));
		if(output==null)
			throw new InvalidRecipeException("cannot load"+id+": no output found!");
	}
	public static DoliumRecipe testPot(FluidStack fluidStack) {
		return recipes.stream().filter(t->t.test(fluidStack,ItemStack.EMPTY)).findFirst().orElse(null);
	}
	public static boolean testInput(ItemStack stack) {
		return recipes.stream().anyMatch(t->t.items.stream().anyMatch(i->i.getFirst().test(stack)));
	}
	public static boolean testContainer(ItemStack stack) {
		return recipes.stream().map(t->t.extra).filter(Objects::nonNull).anyMatch(t->t.test(stack));
	}
	public static DoliumRecipe testDolium(FluidStack f,ItemStackHandler inv) {
		ItemStack is0=inv.getStackInSlot(0);
		ItemStack is1=inv.getStackInSlot(1);
		ItemStack is2=inv.getStackInSlot(2);
		ItemStack cont=inv.getStackInSlot(4);
		return recipes.stream().filter(t->t.test(f,cont,is0,is1,is2)).findFirst().orElse(null);
	}
	public boolean test(FluidStack f,ItemStack container,ItemStack... ss) {
		if(items.size()>0) {
			if(ss.length<items.size())return false;
			int notEmpty=0;
			for(ItemStack is:ss)
				if(!is.isEmpty())
					notEmpty++;
			if(notEmpty<items.size())return false;
		}
		if(extra!=null&&!extra.test(container))return false;
		if(fluid.isSame(Fluids.EMPTY)&&f.isEmpty()) {
		}else if(!f.getFluid().isSame(fluid))return false;
		if(f.getAmount()<amount)return false;
	
		if(density!=0||base!=null) {
			SoupInfo info=SoupFluid.getInfo(f);
			if(base!=null&&!info.base.equals(base))return false;
			if(info.getDensity()<density)return false;
		}
		for(Pair<Ingredient, Integer> igd:items) {
			boolean flag=false;
			for(ItemStack is:ss) {
				if(igd.getFirst().test(is)&&is.getCount()>=igd.getSecond()) {
					flag=true;
					break;
				}
			}
			if(!flag)return false;
		}
		return true;
	}
	public ItemStack handle(FluidStack f) {
		int times=f.getAmount()/amount;
		ItemStack out=output.copy();
		out.setCount(out.getCount()*times);
		if(keepInfo) {
			SoupInfo info=SoupFluid.getInfo(f);
			StewItem.setInfo(out,info);
		}
		f.shrink(times*amount);
		return out;
	}
	public ItemStack handleDolium(FluidStack f,ItemStackHandler inv) {
		int times=Math.min(f.getAmount()/amount,output.getMaxStackSize());
		if(extra!=null)
			times=Math.min(times,inv.getStackInSlot(3).getCount());
		for(Pair<Ingredient, Integer> igd:items) {
			if(igd.getSecond()==0)continue;
			for(int i=0;i<3;i++) {
				ItemStack is=inv.getStackInSlot(i);
				if(igd.getFirst().test(is)) {
					times=Math.min(times,is.getCount()/igd.getSecond());
					break;
				}
			}
		}
		
		if(extra!=null)
			inv.getStackInSlot(4).shrink(times);
		for(Pair<Ingredient, Integer> igd:items) {
			if(igd.getSecond()==0)continue;
			for(int i=0;i<3;i++) {
				ItemStack is=inv.getStackInSlot(i);
				if(igd.getFirst().test(is)) {
					is.shrink(times*igd.getSecond());
					break;
				}
			}
		}
		ItemStack out=output.copy();
		out.setCount(out.getCount()*times);
		if(keepInfo) {
			SoupInfo info=SoupFluid.getInfo(f);
			StewItem.setInfo(out,info);
		}
		f.shrink(times*amount);
		return out;
	}
	public DoliumRecipe(ResourceLocation id, FriendlyByteBuf data) {
		super(id);
		items = SerializeUtil.readList(data,d->Pair.of(Ingredient.fromNetwork(d),d.readVarInt()));
		base=SerializeUtil.readOptional(data,FriendlyByteBuf::readResourceLocation).orElse(null);
		fluid=data.readRegistryIdUnsafe(ForgeRegistries.FLUIDS);
		amount=data.readVarInt();
		density=data.readFloat();
		keepInfo=data.readBoolean();
		output=data.readItem();
		extra=SerializeUtil.readOptional(data,Ingredient::fromNetwork).orElse(null);
	}

	public void write(FriendlyByteBuf data) {
		SerializeUtil.writeList(data, items,(r,d)->{r.getFirst().toNetwork(data);data.writeVarInt(r.getSecond());});
		SerializeUtil.writeOptional2(data,base,FriendlyByteBuf::writeResourceLocation);
		data.writeRegistryIdUnsafe(ForgeRegistries.FLUIDS, fluid);
		data.writeVarInt(amount);
		data.writeFloat(density);
		data.writeBoolean(keepInfo);
		data.writeItem(output);
		SerializeUtil.writeOptional(data, extra, Ingredient::toNetwork);
	}

	@Override
	public void serializeRecipeData(JsonObject json) {
		json.add("items",SerializeUtil.toJsonList(items,(r)->{JsonObject jo=new JsonObject();jo.add("item",r.getFirst().toJson());jo.addProperty("count",r.getSecond());return jo;}));
		if(base!=null)
		json.addProperty("base",base.toString());
		if(!fluid.isSame(Fluids.EMPTY))
			json.addProperty("fluid",fluid.getRegistryName().toString());
		json.addProperty("density", density);
		json.addProperty("amount", amount);
		json.addProperty("keepInfo",keepInfo);
		json.add("output",NBTIngredient.of(output).toJson());
		if(extra!=null)
			json.add("container",extra.toJson());
	}

}
