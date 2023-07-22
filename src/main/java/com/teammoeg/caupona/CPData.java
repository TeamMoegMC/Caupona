package com.teammoeg.caupona;

import com.mojang.serialization.Codec;
import com.teammoeg.caupona.data.loot.AddPoolLootModifier;

import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries.Keys;

public class CPData {
	public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS=DeferredRegister.create(Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, CPMain.MODID);
	static {
		LOOT_MODIFIERS.register("add_table",()->AddPoolLootModifier.CODEC);
	}
}
