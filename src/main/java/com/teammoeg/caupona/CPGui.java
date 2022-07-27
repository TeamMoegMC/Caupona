package com.teammoeg.caupona;

import com.teammoeg.caupona.blocks.dolium.DoliumContainer;
import com.teammoeg.caupona.blocks.pot.StewPotContainer;
import com.teammoeg.caupona.blocks.stove.KitchenStoveContainer;
import com.teammoeg.caupona.container.PortableBrazierContainer;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CPGui {
	public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister
			.create(ForgeRegistries.CONTAINERS, Main.MODID);
	public static final RegistryObject<MenuType<StewPotContainer>> STEWPOT = CONTAINERS.register("stew_pot",
			() -> IForgeMenuType.create(StewPotContainer::new));
	public static final RegistryObject<MenuType<KitchenStoveContainer>> STOVE = CONTAINERS.register("kitchen_stove",
			() -> IForgeMenuType.create(KitchenStoveContainer::new));
	public static final RegistryObject<MenuType<DoliumContainer>> DOLIUM = CONTAINERS.register("dolium",
			() -> IForgeMenuType.create(DoliumContainer::new));
	public static final RegistryObject<MenuType<PortableBrazierContainer>> BRAZIER = CONTAINERS.register("portable_brazier",
			() -> IForgeMenuType.create(PortableBrazierContainer::new));
	
}