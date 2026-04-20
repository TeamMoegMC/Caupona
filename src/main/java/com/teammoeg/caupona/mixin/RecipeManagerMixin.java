package com.teammoeg.caupona.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.teammoeg.caupona.data.RecipeReloadListener;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.RecipeManager;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {

	public RecipeManagerMixin() {
	}
	@Inject(at = @At(value = "TAIL", remap = true), method = "Lnet/minecraft/world/item/crafting/RecipeManager;finalizeRecipeLoading(Lnet/minecraft/world/flag/FeatureFlagSet;)V", remap = true, cancellable = true, require = 1, allow = 1)
	public void finishReload(FeatureFlagSet set, CallbackInfo cbi) {
		//RecipeReloadListener.buildRecipeLists((RecipeManager)(Object)this);
		
	}
}
