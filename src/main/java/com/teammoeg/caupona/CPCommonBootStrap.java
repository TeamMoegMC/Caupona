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
 * Specially, we allow this software to be used alongside with closed source software Minecraft(R) and Forge or other modloader.
 * Any mods or plugins can also use apis provided by forge or com.teammoeg.caupona.api without using GPL or open source.
 *
 * You should have received a copy of the GNU General Public License
 * along with Caupona. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.caupona;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.mojang.datafixers.util.Pair;
import com.teammoeg.caupona.api.CauponaApi;
import com.teammoeg.caupona.api.events.ContanerContainFoodEvent;
import com.teammoeg.caupona.blocks.dolium.CounterDoliumBlockEntity;
import com.teammoeg.caupona.blocks.foods.IFoodContainer;
import com.teammoeg.caupona.blocks.pan.GravyBoatBlock;
import com.teammoeg.caupona.blocks.pan.PanBlockEntity;
import com.teammoeg.caupona.blocks.pot.StewPotBlockEntity;
import com.teammoeg.caupona.entity.CPBoat;
import com.teammoeg.caupona.util.CreativeTabItemHelper;
import com.teammoeg.caupona.util.ICreativeModeTabItem;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.wrappers.BucketPickupHandlerWrapper;
import net.minecraftforge.fluids.capability.wrappers.FluidBlockWrapper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = CPMain.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CPCommonBootStrap {
	public static final List<Pair<Supplier<? extends ItemLike>, Float>> compositables = new ArrayList<>();
	public static final List<Pair<Supplier<? extends Block>,Pair<Integer,Integer>>> flamables=new ArrayList<>();
	@SubscribeEvent
	public static void onCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
		CreativeTabItemHelper helper = new CreativeTabItemHelper(event.getTabKey(), event.getTab());
		CPItems.ITEMS.getEntries().forEach(e -> {
			if (e.get() instanceof ICreativeModeTabItem item) {
				item.fillItemCategory(helper);
			}
		});
		helper.register(event);

	}

	public static <T extends ItemLike> RegistryObject<T> asCompositable(RegistryObject<T> obj, float val) {
		compositables.add(Pair.of(obj, val));
		return obj;
	}
	public static <T extends Block> RegistryObject<T> asFlamable(RegistryObject<T> obj,int v1,int v2) {
		flamables.add(Pair.of(obj, Pair.of(v1, v2)));
		return obj;
	}
	
	@SubscribeEvent
	public static void onCommonSetup(@SuppressWarnings("unused") FMLCommonSetupEvent event) {
		registerDispensers();
		compositables.forEach(p -> ComposterBlock.COMPOSTABLES.put(p.getFirst().get(), (float) p.getSecond()));
		FireBlock fire=(FireBlock) Blocks.FIRE;
		flamables.forEach(p->fire.setFlammable(p.getFirst().get(), p.getSecond().getFirst(), p.getSecond().getSecond()));
	}

	public static void registerDispensers() {
		DispenserBlock.registerBehavior(Items.BOWL, new DefaultDispenseItemBehavior() {
			private final DefaultDispenseItemBehavior defaultBehaviour = new DefaultDispenseItemBehavior();

			@SuppressWarnings("resource")
			@Override
			protected ItemStack execute(BlockSource bp, ItemStack is) {

				Direction d = bp.getBlockState().getValue(DispenserBlock.FACING);
				BlockPos front = bp.getPos().relative(d);
				FluidState fs = bp.getLevel().getBlockState(front).getFluidState();
				BlockEntity blockEntity = bp.getLevel().getBlockEntity(front);
				if (blockEntity != null) {
					LazyOptional<IFluidHandler> ip = blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER,
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
					} else if (blockEntity instanceof PanBlockEntity pan) {
						ItemStack out = pan.inv.getStackInSlot(10);
						if (!out.isEmpty()) {
							pan.inv.setStackInSlot(10, ItemStack.EMPTY);
							if (bp.<DispenserBlockEntity>getEntity().addItem(out) == -1)
								this.defaultBehaviour.dispense(bp, out);
						}
					}

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
		DispenserBlock.registerBehavior(CPItems.redstone_ladle.get(), new DefaultDispenseItemBehavior() {
			@SuppressWarnings("resource")
			@Override
			protected ItemStack execute(BlockSource bp, ItemStack is) {

				Direction d = bp.getBlockState().getValue(DispenserBlock.FACING);
				BlockPos front = bp.getPos().relative(d);
				BlockPos back = bp.getPos().relative(d.getOpposite());
				Block src = bp.getLevel().getBlockState(front).getBlock();
				LazyOptional<IFluidHandler> blockSource = FluidUtil.getFluidHandler(bp.getLevel(), front,
						d.getOpposite());
				BlockEntity besrc=bp.getLevel().getBlockEntity(front);
				BlockEntity blockTarget = bp.getLevel().getBlockEntity(back);
				if (blockTarget != null) {
					LazyOptional<IFluidHandler> iptar = blockTarget.getCapability(ForgeCapabilities.FLUID_HANDLER, d);
					if (iptar.isPresent()) {
						if (blockSource.isPresent()) {
							FluidUtil.tryFluidTransfer(iptar.orElse(null), blockSource.orElse(null), 250, true);

						} else if (src instanceof BucketPickup bpu) {
							FluidUtil.tryFluidTransfer(iptar.orElse(null),
									new BucketPickupHandlerWrapper(bpu, bp.getLevel(), front), FluidType.BUCKET_VOLUME,
									true);
						} else if (src instanceof IFluidBlock bpu) {
							FluidUtil.tryFluidTransfer(iptar.orElse(null),
									new FluidBlockWrapper(bpu, bp.getLevel(), front), Integer.MAX_VALUE, true);
						}else if(besrc instanceof IFoodContainer cont) {
							for(int i=0;i<cont.getSlots();i++) {
								ItemStack its=cont.getInternal(i);
								FluidStack fs=Utils.extractFluid(its);
								if(!fs.isEmpty()) {
									if(iptar.orElse(null).fill(fs, FluidAction.SIMULATE)==fs.getAmount()) {
										iptar.orElse(null).fill(fs, FluidAction.EXECUTE);
										cont.setInternal(i,its.getCraftingRemainingItem());
										break;
									}
								}
								
							}
						}
					}else if(blockTarget instanceof IFoodContainer contt) {
						LazyOptional<IFluidHandler> ipsrc = besrc.getCapability(ForgeCapabilities.FLUID_HANDLER, d.getOpposite());
						if(besrc instanceof IFoodContainer cont) {
							outer:for(int i=0;i<cont.getSlots();i++) {
								ItemStack its=cont.getInternal(i);
								
								if(!its.isEmpty()&&Utils.isExtractAllowed(its)) {
									for(int j=0;j<contt.getSlots();j++) {
										ItemStack its2=contt.getInternal(j);
										if(Utils.isExchangeAllowed(its, its2)&&cont.accepts(i, its2)&&contt.accepts(j, its)) {
											cont.setInternal(i, its2);
											contt.setInternal(j, its);
											break outer;
										}
									}
								}
							}
						}else if(ipsrc.isPresent()){
							IFluidHandler tank=ipsrc.orElse(null);
							
							FluidStack fs=tank.drain(250, FluidAction.SIMULATE);
							if(!fs.isEmpty()) {
								for(int j=0;j<contt.getSlots();j++) {
									ItemStack its2=contt.getInternal(j);
									if(its2.getCount()==1) {
										ContanerContainFoodEvent ev=Utils.contain(its2, fs,true);
										if(ev.isAllowed()) {
											if(contt.accepts(j, ev.out)) {
												fs=tank.drain(ev.drainAmount, FluidAction.EXECUTE);
												if(fs.getAmount()==ev.drainAmount) {
													ev=Utils.contain(its2, fs,false);
													contt.setInternal(j,ev.out);
												}
											}
											break;
										}
									}
								}
							}
						}
						
					}

					return is;
				}
				return is;
			}

		});
		DispenserBlock.registerBehavior(CPItems.walnut_boat.get(), new DefaultDispenseItemBehavior() {
			private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

			public ItemStack execute(BlockSource pSource, ItemStack pStack) {
				Direction direction = pSource.getBlockState().getValue(DispenserBlock.FACING);
				Level level = pSource.getLevel();
				double d0 = pSource.x() + direction.getStepX() * 1.125F;
				double d1 = pSource.y() + direction.getStepY() * 1.125F;
				double d2 = pSource.z() + direction.getStepZ() * 1.125F;
				BlockPos blockpos = pSource.getPos().relative(direction);
				double d3;
				if (level.getFluidState(blockpos).is(FluidTags.WATER)) {
					d3 = 1.0D;
				} else {
					if (!level.getBlockState(blockpos).isAir()
							|| !level.getFluidState(blockpos.below()).is(FluidTags.WATER)) {
						return this.defaultDispenseItemBehavior.dispense(pSource, pStack);
					}

					d3 = 0.0D;
				}

				Boat boat = new CPBoat(level, d0, d1 + d3, d2);
				boat.setYRot(direction.toYRot());
				level.addFreshEntity(boat);
				pStack.shrink(1);
				return pStack;
			}

			protected void playSound(BlockSource pSource) {
				pSource.getLevel().levelEvent(1000, pSource.getPos(), 0);
			}
		});
		DispenserBlock.registerBehavior(CPItems.gravy_boat.get(), new DefaultDispenseItemBehavior() {
			private final DefaultDispenseItemBehavior defaultBehaviour = new DefaultDispenseItemBehavior();

			@SuppressWarnings("resource")
			@Override
			protected ItemStack execute(BlockSource bp, ItemStack is) {

				Direction d = bp.getBlockState().getValue(DispenserBlock.FACING);
				BlockPos front = bp.getPos().relative(d);
				BlockState bs = bp.getLevel().getBlockState(front);
				if (bs.is(CPBlocks.GRAVY_BOAT.get())) {
					int idmg = is.getDamageValue();
					is.setDamageValue(bs.getValue(GravyBoatBlock.LEVEL));
					bp.getLevel().setBlockAndUpdate(front, bs.setValue(GravyBoatBlock.LEVEL, idmg));
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
			@SuppressWarnings("resource")
			public ItemStack execute(BlockSource source, ItemStack stack) {

				Level world = source.getLevel();
				Direction d = source.getBlockState().getValue(DispenserBlock.FACING);
				BlockPos front = source.getPos().relative(d);
				BlockEntity blockEntity = world.getBlockEntity(front);
				if (blockEntity != null) {
					LazyOptional<IFluidHandler> ip = blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER,
							d.getOpposite());
					if (ip.isPresent()) {
						FluidActionResult fa = FluidUtil.tryEmptyContainerAndStow(stack, ip.resolve().get(), null, 1250,
								null, true);
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

			@SuppressWarnings("resource")
			@Override
			protected ItemStack execute(BlockSource source, ItemStack stack) {
				FluidStack fs = Utils.extractFluid(stack);
				Direction d = source.getBlockState().getValue(DispenserBlock.FACING);
				BlockPos front = source.getPos().relative(d);
				BlockEntity blockEntity = source.getLevel().getBlockEntity(front);

				if (!fs.isEmpty()) {
					if (blockEntity instanceof StewPotBlockEntity pot) {
						if (pot.tryAddFluid(fs)) {
							ItemStack ret = stack.getCraftingRemainingItem();
							if (stack.getCount() == 1)
								return ret;
							stack.shrink(1);
							if (source.<DispenserBlockEntity>getEntity().addItem(ret) == -1)
								this.defaultBehaviour.dispense(source, ret);
						}
					} else if (blockEntity != null) {
						LazyOptional<IFluidHandler> ip = blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER,
								d.getOpposite());
						if (ip.isPresent()) {
							IFluidHandler handler = ip.resolve().get();
							if (handler.fill(fs, FluidAction.SIMULATE) == fs.getAmount()) {
								handler.fill(fs, FluidAction.EXECUTE);
								ItemStack ret = stack.getCraftingRemainingItem();
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
		DefaultDispenseItemBehavior spice = new DefaultDispenseItemBehavior() {
			private final DefaultDispenseItemBehavior defaultBehaviour = new DefaultDispenseItemBehavior();

			@SuppressWarnings("resource")
			@Override
			protected ItemStack execute(BlockSource source, ItemStack stack) {
				Direction d = source.getBlockState().getValue(DispenserBlock.FACING);
				BlockPos front = source.getPos().relative(d);
				BlockEntity blockEntity = source.getLevel().getBlockEntity(front);

				if (blockEntity instanceof StewPotBlockEntity pot) {
					ItemStack ospice = pot.getInv().getStackInSlot(11);
					pot.getInv().setStackInSlot(11, stack);
					return ospice;
				} else if (blockEntity instanceof PanBlockEntity pan) {
					ItemStack ospice = ((PanBlockEntity) blockEntity).getInv().getStackInSlot(11);
					pan.getInv().setStackInSlot(11, stack);
					return ospice;
				} else if (blockEntity instanceof CounterDoliumBlockEntity dolium) {
					ItemStack ospice = dolium.getInv().getStackInSlot(3);
					dolium.getInv().setStackInSlot(3, stack);
					return ospice;
				}

				return this.defaultBehaviour.dispense(source, stack);
			}

		};
		DefaultDispenseItemBehavior pot = new DefaultDispenseItemBehavior() {
			private final DefaultDispenseItemBehavior defaultBehaviour = new DefaultDispenseItemBehavior();

			@SuppressWarnings("resource")
			@Override
			protected ItemStack execute(BlockSource source, ItemStack stack) {
				Direction d = source.getBlockState().getValue(DispenserBlock.FACING);
				BlockPos front = source.getPos().relative(d);
				BlockEntity blockEntity = source.getLevel().getBlockEntity(front);

				if (blockEntity instanceof StewPotBlockEntity pot) {
					ItemStack ospice = pot.getInv().getStackInSlot(11);
					pot.getInv().setStackInSlot(11, ItemStack.EMPTY);
					if (source.<DispenserBlockEntity>getEntity().addItem(ospice) == -1)
						this.defaultBehaviour.dispense(source, ospice);
					return stack;
				} else if (blockEntity instanceof PanBlockEntity pan) {
					ItemStack ospice = pan.getInv().getStackInSlot(11);
					pan.getInv().setStackInSlot(11, ItemStack.EMPTY);
					if (source.<DispenserBlockEntity>getEntity().addItem(ospice) == -1)
						this.defaultBehaviour.dispense(source, ospice);
					return stack;
				} else if (blockEntity instanceof CounterDoliumBlockEntity dolium) {
					ItemStack ospice = dolium.getInv().getStackInSlot(3);
					dolium.getInv().setStackInSlot(3, ItemStack.EMPTY);
					if (source.<DispenserBlockEntity>getEntity().addItem(ospice) == -1)
						this.defaultBehaviour.dispense(source, ospice);
					return stack;
				}

				return this.defaultBehaviour.dispense(source, stack);
			}

		};
		DispenserBlock.registerBehavior(Items.FLOWER_POT, pot);
		for (RegistryObject<Item> i : CPItems.spicesItems) {
			DispenserBlock.registerBehavior(i.get(), spice);
		}
	}

}
