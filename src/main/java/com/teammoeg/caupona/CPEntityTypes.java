package com.teammoeg.caupona;

import com.teammoeg.caupona.entity.CPBoat;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CPEntityTypes {
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES,
			Main.MODID);
	public static final RegistryObject<EntityType<CPBoat>> BOAT = ENTITY_TYPES.register("boat",
			() -> EntityType.Builder.<CPBoat>of(CPBoat::new, MobCategory.MISC).sized(0.5f, 0.5f)
					.build(new ResourceLocation(Main.MODID, "boat").toString()));
	
	private CPEntityTypes() {
	}

}
