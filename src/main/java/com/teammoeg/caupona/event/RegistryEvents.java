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

package com.teammoeg.caupona.event;

import java.util.ArrayList;
import java.util.List;

import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPItems;
import com.teammoeg.caupona.CPRecipes;
import com.teammoeg.caupona.Main;
import com.teammoeg.caupona.api.CauponaApi;
import com.teammoeg.caupona.blocks.StewPotTileEntity;
import com.teammoeg.caupona.data.recipes.BowlContainingRecipe;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryEvents {
	public static List<Block> registeredBlocks = new ArrayList<>();
	public static List<Item> registeredItems = new ArrayList<>();
	public static List<Fluid> registeredFluids = new ArrayList<>();
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		CPBlocks.init();
		for (Block block : RegistryEvents.registeredBlocks) {
			try {
				event.getRegistry().register(block);
			} catch (Throwable e) {
				Main.logger.error("Failed to register a block. ({})", block);
				throw e;
			}
		}
	}

	


	@SubscribeEvent
	public void setup(final FMLCommonSetupEvent event) {
		
		
	}
	@SubscribeEvent
	public static void registerRecipeTypes(RegistryEvent.Register<?> event) {
		CPRecipes.registerRecipeTypes();
	}
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		CPItems.init();
		for (Item item : RegistryEvents.registeredItems) {
			try {
				event.getRegistry().register(item);
			} catch (Throwable e) {
				Main.logger.error("Failed to register an item. ({}, {})", item, item.getRegistryName());
				throw e;
			}
		}
		DispenserBlock.registerBehavior(Items.BOWL, new DefaultDispenseItemBehavior() {
			private final DefaultDispenseItemBehavior defaultBehaviour = new DefaultDispenseItemBehavior();

			@Override
			protected ItemStack execute(BlockSource bp, ItemStack is) {

				Direction d = bp.getBlockState().getValue(DispenserBlock.FACING);
				BlockPos front = bp.getPos().relative(d);
				FluidState fs = bp.getLevel().getBlockState(front).getFluidState();
				BlockEntity te = bp.getLevel().getBlockEntity(front);
				if (te != null) {
					LazyOptional<IFluidHandler> ip = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,
							d.getOpposite());
					if (ip.isPresent()) {
						ItemStack ret = CauponaApi.fillBowl(ip.resolve().get()).orElse(null);
						if (ret != null) {
							if (is.getCount() == 1)
								return ret;
							is.shrink(1);
							if (bp.<DispenserBlockEntity>getEntity().addItem(ret) == -1)
								this.defaultBehaviour.dispense(bp, ret);
						}
					}
					;
					return is;
				} else if (!fs.isEmpty()) {
					ItemStack ret = CauponaApi.fillBowl(new FluidStack(fs.getType(), 250)).orElse(null);
					if (ret != null) {
						if (is.getCount() == 1)
							return ret;
						is.shrink(1);
						if (bp.<DispenserBlockEntity>getEntity().addItem(ret) == -1)
							this.defaultBehaviour.dispense(bp, ret);
					}
					return is;
				}
				return this.defaultBehaviour.dispense(bp, is);
			}

		});
		DispenseItemBehavior idispenseitembehavior1 = new DefaultDispenseItemBehavior() {
			private final DefaultDispenseItemBehavior defaultBehaviour = new DefaultDispenseItemBehavior();

			/**
			 * Dispense the specified stack, play the dispense sound and spawn particles.
			 */
			public ItemStack execute(BlockSource source, ItemStack stack) {
				
				BlockPos blockpos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
				Level world = source.getLevel();
				Direction d = source.getBlockState().getValue(DispenserBlock.FACING);
				BlockPos front = source.getPos().relative(d);
				FluidState fs = world.getBlockState(front).getFluidState();
				BlockEntity te = world.getBlockEntity(front);
				if (te != null) {
					LazyOptional<IFluidHandler> ip = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,
							d.getOpposite());
					if (ip.isPresent()) {
						FluidActionResult fa = FluidUtil.tryEmptyContainerAndStow(stack,ip.resolve().get(), null, 1250,null,
								true);
						if (fa.isSuccess()) {
							if (fa.getResult() != null)
								return fa.getResult();
							stack.shrink(1);
							
						}
					}
					return stack;
				}
				return this.defaultBehaviour.dispense(source, stack);
			}
		};
		DispenserBlock.registerBehavior(Items.MILK_BUCKET, idispenseitembehavior1);
		DefaultDispenseItemBehavior ddib = new DefaultDispenseItemBehavior() {
			private final DefaultDispenseItemBehavior defaultBehaviour = new DefaultDispenseItemBehavior();

			@Override
			protected ItemStack execute(BlockSource source, ItemStack stack) {
				FluidStack fs = BowlContainingRecipe.extractFluid(stack);
				Direction d = source.getBlockState().getValue(DispenserBlock.FACING);
				BlockPos front = source.getPos().relative(d);
				BlockEntity te = source.getLevel().getBlockEntity(front);

				if (!fs.isEmpty()) {
					if (te instanceof StewPotTileEntity) {
						if (((StewPotTileEntity) te).tryAddFluid(fs)) {
							ItemStack ret = stack.getContainerItem();
							if (stack.getCount() == 1)
								return ret;
							stack.shrink(1);
							if (source.<DispenserBlockEntity>getEntity().addItem(ret) == -1)
								this.defaultBehaviour.dispense(source, ret);
						}
					} else if (te != null) {
						LazyOptional<IFluidHandler> ip = te
								.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, d.getOpposite());
						if (ip.isPresent()) {
							IFluidHandler handler = ip.resolve().get();
							if (handler.fill(fs, FluidAction.SIMULATE) == fs.getAmount()) {
								handler.fill(fs, FluidAction.EXECUTE);
								ItemStack ret = stack.getContainerItem();
								if (stack.getCount() == 1)
									return ret;
								stack.shrink(1);
								if (source.<DispenserBlockEntity>getEntity().addItem(ret) == -1)
									this.defaultBehaviour.dispense(source, ret);
							}
						}
					}
					return stack;
				}
				return this.defaultBehaviour.dispense(source, stack);
			}

		};
		for (Item i : CPItems.stews) {
			DispenserBlock.registerBehavior(i, ddib);
		}
	}

	@SubscribeEvent
	public static void registerFluids(RegistryEvent.Register<Fluid> event) {
		
		for (Fluid fluid : RegistryEvents.registeredFluids) {
			try {
				event.getRegistry().register(fluid);
			} catch (Throwable e) {
				Main.logger.error("Failed to register a fluid. ({}, {})", fluid, fluid.getRegistryName());
				throw e;
			}
		}
	}

	@SuppressWarnings("unused")
	@SubscribeEvent
	public static void registerEffects(final RegistryEvent.Register<MobEffect> event) {

	}

}
