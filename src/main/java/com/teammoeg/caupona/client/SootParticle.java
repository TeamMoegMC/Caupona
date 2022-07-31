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

package com.teammoeg.caupona.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

public class SootParticle extends CPParticle {

	public SootParticle(ClientLevel world, double x, double y, double z, double motionX, double motionY,
			double motionZ) {
		super(world, x, y, z, motionX, motionY, motionZ);
		this.gravity = -0.1F;
		this.rCol = this.gCol = this.bCol = (float) (Math.random() * 0.2) + 0.8f;
		this.originalScale = 0.25F;
		this.lifetime = (int) (20.0D / (Math.random() * 0.8D + 0.2D));
		super.alpha = 0.75f;
	}

	public static class Factory implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteSet;

		public Factory(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Override
		public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z,
				double xSpeed, double ySpeed, double zSpeed) {
			SootParticle steamParticle = new SootParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
			steamParticle.pickSprite(this.spriteSet);
			return steamParticle;
		}
	}
}
