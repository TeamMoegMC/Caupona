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

package com.teammoeg.caupona.util;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import java.util.stream.Stream;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;

public class FloatemStack {
	ItemStack stack;
	float count;

	public FloatemStack(ItemStack stack, float count) {
		super();
		this.stack = stack.copy();
		this.stack.setCount(1);
		this.count = count;
	}

	public FloatemStack(CompoundTag nbt) {
		super();
		this.deserializeNBT(nbt);
	}

	public FloatemStack(ItemStack is) {
		this(is, is.getCount());
	}

	public ItemStack getStack() {
		return stack.copy();
	}

	public ItemStack getContainerItem() {
		return stack.getContainerItem();
	}

	public boolean hasContainerItem() {
		return stack.hasContainerItem();
	}

	public CompoundTag serializeNBT() {
		CompoundTag cnbt = stack.serializeNBT();
		cnbt.putFloat("th_countf", count);
		return cnbt;
	}

	public boolean isEmpty() {
		return count <= 0.001;
	}

	public Item getItem() {
		return stack.getItem();
	}

	public int getEntityLifespan(Level world) {
		return stack.getEntityLifespan(world);
	}

	public CompoundTag write(CompoundTag nbt) {
		CompoundTag cnbt = stack.save(nbt);
		cnbt.putFloat("th_countf", count);
		return cnbt;
	}

	public int getMaxStackSize() {
		return stack.getMaxStackSize();
	}

	public boolean isStackable() {
		return stack.isStackable();
	}

	public boolean isDamageable() {
		return stack.isDamageableItem();
	}

	public boolean isDamaged() {
		return stack.isDamaged();
	}

	public int getDamage() {
		return stack.getDamageValue();
	}

	public void setDamage(int damage) {
		stack.setDamageValue(damage);
	}

	public int getMaxDamage() {
		return stack.getMaxDamage();
	}

	public CompoundTag getShareTag() {
		return stack.getShareTag();
	}

	public void readShareTag(CompoundTag nbt) {
		stack.readShareTag(nbt);
	}

	public boolean areShareTagsEqual(ItemStack other) {
		return stack.areShareTagsEqual(other);
	}

	public FloatemStack copy() {
		return new FloatemStack(stack.copy(), this.count);
	}

	public boolean isItemEqual(ItemStack other) {
		return stack.sameItem(other);
	}

	public boolean isItemEqualIgnoreDurability(ItemStack stack) {
		return stack.sameItemStackIgnoreDurability(stack);
	}

	public String getTranslationKey() {
		return stack.getDescriptionId();
	}

	public boolean hasTag() {
		return stack.hasTag();
	}

	public CompoundTag getTag() {
		return stack.getTag();
	}
	public Stream<ResourceLocation> getTags() {
		return stack.getTags().map(TagKey::location);
	}
	public CompoundTag getOrCreateTag() {
		return stack.getOrCreateTag();
	}

	public CompoundTag getOrCreateChildTag(String key) {
		return stack.getOrCreateTagElement(key);
	}

	public CompoundTag getChildTag(String key) {
		return stack.getTagElement(key);
	}

	public void removeChildTag(String p_196083_1_) {
		stack.removeTagKey(p_196083_1_);
	}

	public void setTag(CompoundTag nbt) {
		stack.setTag(nbt);
	}

	public Component getDisplayName() {
		return stack.getHoverName();
	}

	public ItemStack setDisplayName(Component name) {
		return stack.setHoverName(name);
	}

	public void clearCustomName() {
		stack.resetHoverName();
	}

	public boolean hasDisplayName() {
		return stack.hasCustomHoverName();
	}

	public boolean hasEffect() {
		return stack.hasFoil();
	}

	public Rarity getRarity() {
		return stack.getRarity();
	}

	public void setTagInfo(String key, Tag value) {
		stack.addTagElement(key, value);
	}

	public Component getTextComponent() {
		return stack.getDisplayName();
	}

	public float getCount() {
		return count;
	}

	public void setCount(float count) {
		this.count = count;
	}

	public void grow(float count) {
		this.count += count;
	}

	public void shrink(float count) {
		this.count -= count;
		if (this.count < 0)
			this.count = 0;
	}

	public boolean isFood() {
		return stack.isEdible();
	}

	public boolean equals(ItemStack other) {
		if (this.getItem() != other.getItem()) {
			return false;
		}
		return ItemStack.tagMatches(this.getStack(), other);
	}

	public void deserializeNBT(CompoundTag nbt) {
		stack = ItemStack.of(nbt);
		this.count = nbt.getFloat("th_countf");
	}
}
