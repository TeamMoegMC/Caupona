package com.teammoeg.caupona.mixin;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.teammoeg.caupona.client.Particles;
import com.teammoeg.caupona.util.ChimneyHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SmokerBlock;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(SmokerBlock.class)
public class SmokerBlockMixin {

	public SmokerBlockMixin() {
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V", remap = true, ordinal = 0), method = "animateTick", remap = true, cancellable = true, require = 1, allow = 1)
	public void cp$animateTick(BlockState pState, Level pLevel, BlockPos pPos, Random pRandom, CallbackInfo cbi) {
		BlockPos bp = ChimneyHelper.getNearestChimney(pLevel, pPos, 2);
		if (bp != null) {
			double motY = -0.3, delY = .5;
			if (!pLevel.getBlockState(bp).is(ChimneyHelper.chimney_pot)) {
				motY = pRandom.nextDouble() * .25;
				delY = 0;
			}
			pLevel.addParticle(Particles.SOOT.get(), bp.getX() + .5, bp.getY() + delY, bp.getZ() + .5,
					pRandom.nextDouble() * .5 - .25, motY, pRandom.nextDouble() * .5 - .25);
			cbi.cancel();
		}
	}
}
