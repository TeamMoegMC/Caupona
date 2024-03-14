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

package com.teammoeg.caupona.util;

import java.util.Objects;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class SpicedFoodInfo implements INBTSerializable<Tag>{
	public MobEffectInstance spice;
	public boolean hasSpice = false;
	public ResourceLocation spiceName;

	public SpicedFoodInfo() {
	}

	public static ResourceLocation getSpice(CompoundTag nbt) {
		if (nbt.contains("spiceName"))
			return new ResourceLocation(nbt.getString("spiceName"));
		return null;
	}

	public boolean addSpice(MobEffectInstance spice, ItemStack im) {
		if (this.spice != null)
			return false;
		this.spice = new MobEffectInstance(spice);
		hasSpice = true;
		this.spiceName =Utils.getRegistryName(im);
		return true;
	}

	public void clearSpice() {
		spice = null;
		hasSpice = false;
		spiceName = null;
	}

	public boolean canAddSpice() {
		return !hasSpice;
	}

	public void write(CompoundTag nbt) {
		nbt.putBoolean("hasSpice", hasSpice);
		if (spice != null)
			nbt.put("spice", spice.save(new CompoundTag()));
		if (spiceName != null)
			nbt.putString("spiceName", spiceName.toString());
	}
	public void read(CompoundTag nbt) {
		hasSpice = nbt.getBoolean("hasSpice");
		if (nbt.contains("spice"))
			spice = MobEffectInstance.load(nbt.getCompound("spice"));
		if (nbt.contains("spiceName"))
			spiceName = new ResourceLocation(nbt.getString("spiceName"));
	}
	
	public CompoundTag save() {
		CompoundTag nbt = new CompoundTag();
		write(nbt);
		return nbt;
	}

	@Override
	public CompoundTag serializeNBT() {
		return save();
	}

	@Override
	public void deserializeNBT(Tag nbt) {
		if(nbt instanceof CompoundTag)
			read((CompoundTag)nbt);
		else
			read(new CompoundTag());
		
	}

	@Override
	public int hashCode() {
		return Objects.hash(hasSpice, spice, spiceName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		SpicedFoodInfo other = (SpicedFoodInfo) obj;
		return hasSpice == other.hasSpice && Objects.equals(spice, other.spice) && Objects.equals(spiceName, other.spiceName);
	}
}
