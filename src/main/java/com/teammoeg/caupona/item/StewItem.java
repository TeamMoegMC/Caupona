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

package com.teammoeg.caupona.item;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPItems;
import com.teammoeg.caupona.util.CreativeTabItemHelper;
import com.teammoeg.caupona.util.FloatemStack;
import com.teammoeg.caupona.util.FluidItemWrapper;
import com.teammoeg.caupona.util.StewInfo;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.registries.ForgeRegistries;

public class StewItem extends EdibleBlock{

	@Override
	public int getUseDuration(ItemStack stack) {
		return 16;
	}

	@Override
	public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		StewInfo info = StewItem.getInfo(stack);
		FloatemStack fs = info.stacks.stream()
				.max((t1, t2) -> t1.getCount() > t2.getCount() ? 1 : (t1.getCount() == t2.getCount() ? 0 : -1))
				.orElse(null);
		if (fs != null)
			tooltip.add(Utils.translate("tooltip.caupona.main_ingredient", fs.getStack().getDisplayName()));
		ResourceLocation rl = info.spiceName;
		if (rl != null)
			tooltip.add(Utils.translate("tooltip.caupona.spice",
					Utils.translate("spice." + rl.getNamespace() + "." + rl.getPath())));
		;
		ResourceLocation base = info.base;
		if (base != null&&!info.stacks.isEmpty())
			tooltip.add(Utils.translate("tooltip.caupona.base", 
					ForgeRegistries.FLUIDS.getValue(base).getFluidType().getDescription()));
		Utils.addPotionTooltip(info.effects, tooltip, 1);
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
	}

	public static StewInfo getInfo(ItemStack stack) {
		if (stack.hasTag()) {
			CompoundTag soupTag = Utils.extractDataElement(stack, "soup");
			return soupTag == null ? new StewInfo(Utils.getFluidTypeRL(stack))
					: new StewInfo(soupTag);
		}
		return new StewInfo();
	}

	public static void setInfo(ItemStack stack, StewInfo si) {
		if (!si.isEmpty())
			Utils.setDataElement(stack,"soup", si.save());
	}

	public static List<FloatemStack> getItems(ItemStack stack) {
	
		CompoundTag nbt = Utils.extractDataElement(stack, "soup");
		if (nbt != null)
			return StewInfo.getStacks(nbt);
		
		return Lists.newArrayList();
	}

	public static ResourceLocation getBase(ItemStack stack) {
		CompoundTag nbt = Utils.extractDataElement(stack, "soup");
		if (nbt != null)
			return new ResourceLocation(StewInfo.getRegName(nbt));
		
		return Utils.getRegistryName(Utils.getFluidType(stack));
	}

	@Override
	public void fillItemCategory(CreativeTabItemHelper helper) {
		if (helper.isFoodTab()) {
			ItemStack is = new ItemStack(this);
			Utils.writeItemFluid(is, fluid);
			super.addCreativeHints(is);
			helper.accept(is);
		}
	}

	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.DRINK;
	}

	ResourceLocation fluid;
	// fake food to trick mechanics
	public static final FoodProperties fakefood = new FoodProperties.Builder().nutrition(4).saturationMod(0.2f).fast()
			.meat().build();

	public StewItem(ResourceLocation fluid, Properties properties) {
		super(CPBlocks.BOWL.get(), properties.food(fakefood));
		CPItems.stews.add(this);
		this.fluid = fluid;
	}


	@Override
	public FoodProperties getFoodProperties(ItemStack stack, LivingEntity entity) {
		return getInfo(stack).getFood();
		
	}

	@Override
	public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
		return new ItemStack(Items.BOWL);
	}

	@Override
	public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		return new FluidItemWrapper(stack);
	}
}
