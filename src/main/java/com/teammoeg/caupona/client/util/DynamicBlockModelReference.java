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

package com.teammoeg.caupona.client.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;


import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;

public record DynamicBlockModelReference(StandaloneModelKey<QuadCollection> name) implements Supplier<QuadCollection>
{

	private static final RandomSource RANDOM_SOURCE=RandomSource.create();
	static {
		RANDOM_SOURCE.setSeed(42L);
	}
	
	public static final Map<String,DynamicBlockModelReference> cache=new HashMap<>();
	private DynamicBlockModelReference(String name)
	{
		this(new StandaloneModelKey<>(()->name));
	}
	public synchronized static DynamicBlockModelReference createKey(String name) {
		return cache.computeIfAbsent(name, DynamicBlockModelReference::new);
		
	}
	public static DynamicBlockModelReference getModelCached(String rl)
	{
		if(rl==null)
			return null;
		return cache.get(rl);
	}
	@Override
	public QuadCollection get()
	{
		return Minecraft.getInstance().getModelManager().getStandaloneModel(name);
	}
	public static RandomSource getRandomSource() {
		return RANDOM_SOURCE;
	}

}