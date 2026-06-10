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

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;

public record DynamicBlockModelReference(StandaloneModelKey<QuadCollection> name) implements Supplier<QuadCollection>
{
	
	public static final Map<Identifier,DynamicBlockModelReference> cache=new HashMap<>();
	private DynamicBlockModelReference(Identifier name)
	{
		this(new StandaloneModelKey<>(name::toString));
	}
	public synchronized static DynamicBlockModelReference createKey(Identifier name) {
		return cache.computeIfAbsent(name, DynamicBlockModelReference::new);
		
	}
	public static DynamicBlockModelReference getModel(Identifier rl)
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
	public void submit(SubmitNodeCollector submitNodeCollector,PoseStack poseStack,RenderType renderType,QuadInstance quadInstance) {
		submitNodeCollector.submitCustomGeometry(poseStack, renderType, (pose,buffer)->{
			for(BakedQuad quad:get().getAll()) {
				buffer.putBakedQuad(pose, quad, quadInstance);
			}
		});
	}

}