package com.teammoeg.caupona.worldgen;

import com.teammoeg.caupona.Main;

import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CPStructures {
	public static final DeferredRegister<StructureFeature<?>> STRUCTURES = DeferredRegister
			.create(ForgeRegistries.STRUCTURE_FEATURES, Main.MODID);

	public static final RegistryObject<StructureFeature<?>> SKY_STRUCTURES = STRUCTURES.register("fumarole",
			FumaroleStructures::new);

	public CPStructures() {
	}

}
