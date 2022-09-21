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

package com.teammoeg.caupona.items;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPItems;
import com.teammoeg.caupona.Main;
import com.teammoeg.caupona.RegistryEvents;
import com.teammoeg.caupona.data.recipes.BowlContainingRecipe;
import com.teammoeg.caupona.util.FloatemStack;
import com.teammoeg.caupona.util.SoupInfo;

import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

public class StewItem extends EdibleBlock {

	@Override
	public int getUseDuration(ItemStack stack) {
		return 16;
	}

	@Override
	public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		SoupInfo info = StewItem.getInfo(stack);
		FloatemStack fs = info.stacks.stream()
				.max((t1, t2) -> t1.getCount() > t2.getCount() ? 1 : (t1.getCount() == t2.getCount() ? 0 : -1))
				.orElse(null);
		if (fs != null)
			tooltip.add(new TranslatableComponent("tooltip.caupona.main_ingredient", fs.getStack().getDisplayName()));
		ResourceLocation rl = info.spiceName;
		if (rl != null)
			tooltip.add(new TranslatableComponent("tooltip.caupona.spice",
					new TranslatableComponent("spice." + rl.getNamespace() + "." + rl.getPath())));
		;
		ResourceLocation base = info.base;
		if (base != null&&!info.stacks.isEmpty())
			tooltip.add(new TranslatableComponent("tooltip.caupona.base", new TranslatableComponent(
					ForgeRegistries.FLUIDS.getValue(base).getAttributes().getTranslationKey())));
		addPotionTooltip(info.effects, tooltip, 1);
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
	}

	public static void addPotionTooltip(List<MobEffectInstance> list, List<Component> lores, float durationFactor) {
		List<Pair<Attribute, AttributeModifier>> list1 = Lists.newArrayList();
		if (!list.isEmpty()) {
			for (MobEffectInstance effectinstance : list) {
				MutableComponent iformattabletextcomponent = new TranslatableComponent(
						effectinstance.getDescriptionId());
				MobEffect effect = effectinstance.getEffect();
				Map<Attribute, AttributeModifier> map = effect.getAttributeModifiers();
				if (!map.isEmpty()) {
					for (Entry<Attribute, AttributeModifier> entry : map.entrySet()) {
						AttributeModifier attributemodifier = entry.getValue();
						AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(),
								effect.getAttributeModifierValue(effectinstance.getAmplifier(), attributemodifier),
								attributemodifier.getOperation());
						list1.add(new Pair<>(entry.getKey(), attributemodifier1));
					}
				}

				if (effectinstance.getAmplifier() > 0) {
					iformattabletextcomponent = new TranslatableComponent("potion.withAmplifier",
							iformattabletextcomponent,
							new TranslatableComponent("potion.potency." + effectinstance.getAmplifier()));
				}

				if (effectinstance.getDuration() > 20) {
					iformattabletextcomponent = new TranslatableComponent("potion.withDuration",
							iformattabletextcomponent, MobEffectUtil.formatDuration(effectinstance, durationFactor));
				}

				lores.add(iformattabletextcomponent.withStyle(effect.getCategory().getTooltipFormatting()));
			}
		}

		if (!list1.isEmpty()) {
			lores.add(TextComponent.EMPTY);
			lores.add((new TranslatableComponent("potion.whenDrank")).withStyle(ChatFormatting.DARK_PURPLE));

			for (Pair<Attribute, AttributeModifier> pair : list1) {
				AttributeModifier attributemodifier2 = pair.getSecond();
				double d0 = attributemodifier2.getAmount();
				double d1;
				if (attributemodifier2.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE
						&& attributemodifier2.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
					d1 = attributemodifier2.getAmount();
				} else {
					d1 = attributemodifier2.getAmount() * 100.0D;
				}

				if (d0 > 0.0D) {
					lores.add((new TranslatableComponent(
							"attribute.modifier.plus." + attributemodifier2.getOperation().toValue(),
							ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1),
							new TranslatableComponent(pair.getFirst().getDescriptionId())))
									.withStyle(ChatFormatting.BLUE));
				} else if (d0 < 0.0D) {
					d1 = d1 * -1.0D;
					lores.add((new TranslatableComponent(
							"attribute.modifier.take." + attributemodifier2.getOperation().toValue(),
							ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1),
							new TranslatableComponent(pair.getFirst().getDescriptionId())))
									.withStyle(ChatFormatting.RED));
				}
			}
		}

	}

	public static SoupInfo getInfo(ItemStack stack) {
		if (stack.hasTag()) {
			CompoundTag soupTag = stack.getTagElement("soup");
			return soupTag == null ? new SoupInfo(new ResourceLocation(stack.getTag().getString("type")))
					: new SoupInfo(soupTag);
		}
		return new SoupInfo();
	}

	public static void setInfo(ItemStack stack, SoupInfo si) {
		if (!si.isEmpty())
			stack.getOrCreateTag().put("soup", si.save());
	}

	public static List<FloatemStack> getItems(ItemStack stack) {
		if (stack.hasTag()) {
			CompoundTag nbt = stack.getTagElement("soup");
			if (nbt != null)
				return SoupInfo.getStacks(nbt);
		}
		return Lists.newArrayList();
	}

	public static ResourceLocation getBase(ItemStack stack) {
		if (stack.hasTag()) {
			CompoundTag nbt = stack.getTagElement("soup");
			if (nbt != null)
				return new ResourceLocation(SoupInfo.getRegName(nbt));
		}
		return BowlContainingRecipe.extractFluid(stack).getFluid().getRegistryName();
	}

	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
		if (this.allowdedIn(group)) {
			ItemStack is = new ItemStack(this);
			is.getOrCreateTag().putString("type", fluid.toString());
			items.add(is);
		}
	}

	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.DRINK;
	}

	ResourceLocation fluid;
	// fake food to trick mechanics
	public static final FoodProperties fakefood = new FoodProperties.Builder().nutrition(4).saturationMod(0.2f).fast()
			.meat().build();

	public StewItem(String name, ResourceLocation fluid, Properties properties) {
		super(CPBlocks.bowl, properties.food(fakefood));
		setRegistryName(Main.MODID, name);
		RegistryEvents.registeredItems.add(this);
		CPItems.stews.add(this);
		this.fluid = fluid;
	}

	@Override
	public FoodProperties getFoodProperties(ItemStack stack, LivingEntity entity) {
		return getInfo(stack).getFood();
		
	}
}
