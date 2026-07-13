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

package com.teammoeg.caupona;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.jspecify.annotations.Nullable;

import com.mojang.datafixers.util.Pair;
import com.teammoeg.caupona.api.CauponaApi;
import com.teammoeg.caupona.api.events.ContanerContainFoodEvent;
import com.teammoeg.caupona.blocks.dolium.CounterDoliumBlockEntity;
import com.teammoeg.caupona.blocks.foods.IFoodContainer;
import com.teammoeg.caupona.blocks.pan.GravyBoatBlock;
import com.teammoeg.caupona.blocks.pan.PanBlockEntity;
import com.teammoeg.caupona.blocks.pot.StewPotBlockEntity;
import com.teammoeg.caupona.blocks.stove.IStove;
import com.teammoeg.caupona.item.SitulaItem;
import com.teammoeg.caupona.network.CPBaseBlockEntity;
import com.teammoeg.caupona.util.CreativeTabItemHelper;
import com.teammoeg.caupona.util.FluidItemWrapper;
import com.teammoeg.caupona.util.ICreativeModeTabItem;
import com.teammoeg.caupona.util.MutableStackItemAccess;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.BoatDispenseItemBehavior;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.ItemAccessFluidHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

@EventBusSubscriber(modid = CPMain.MODID)
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SubscribeEvent
	public static void onCapabilityInject(RegisterCapabilitiesEvent event) {
		event.registerItem(Capabilities.Fluid.ITEM,(_,o)->new ItemAccessFluidHandler(o,CPCapability.SIMPLE_FLUID.get(),SitulaItem.MAX_CAPACITY), CPItems.situla.get());
		//event.registerItem(Capabilities.FluidHandler.ITEM,(stack,o)->new FluidHandlerItemStack(CPCapability.SIMPLE_FLUID,stack,1250), CPItems.situla.get());
		event.registerItem(CPCapability.FOOD_INFO,(stack,_)->stack.get(CPCapability.STEW_INFO.get()), CPItems.stews.toArray(Item[]::new));
		event.registerItem(CPCapability.FOOD_INFO,(stack,_)->stack.get(CPCapability.SAUTEED_INFO.get()), CPItems.dish.toArray(Item[]::new));
		CPBlockEntityTypes.REGISTER.getEntries().stream().map(t->t.get()).forEach(be->{
				event.registerBlockEntity(Capabilities.Item.BLOCK, (BlockEntityType<?>)be,
					(block,ctx)->(block instanceof CPBaseBlockEntity)?(ResourceHandler)((CPBaseBlockEntity)block).getCapability(Capabilities.Item.BLOCK, ctx):null);
				event.registerBlockEntity(Capabilities.Fluid.BLOCK, (BlockEntityType<?>)be,
					(block,ctx)->(block instanceof CPBaseBlockEntity)?(ResourceHandler)((CPBaseBlockEntity)block).getCapability(Capabilities.Fluid.BLOCK, ctx):null);
				event.registerBlockEntity(CPCapability.HEAT_STOVE, (BlockEntityType<?>)be,
						(block,ctx)->(block instanceof CPBaseBlockEntity)?(IStove)((CPBaseBlockEntity)block).getCapability(CPCapability.HEAT_STOVE, ctx):null);
				event.registerBlockEntity(CPCapability.FOOD_CONTAINER, (BlockEntityType<?>)be,
					(block,ctx)->(block instanceof CPBaseBlockEntity)?(IFoodContainer)((CPBaseBlockEntity)block).getCapability(CPCapability.FOOD_CONTAINER, ctx):null);
		
		});
		event.registerItem(Capabilities.Fluid.ITEM,(_,o)->new FluidItemWrapper(o), CPItems.stews.toArray(Item[]::new));
	}

	public static <R extends ItemLike,T extends R> DeferredHolder<R,T> asCompositable(DeferredHolder<R,T> obj, float val) {
		compositables.add(Pair.of(obj, val));
		return obj;
	}


	@SuppressWarnings("deprecation")
	@SubscribeEvent
	public static void onCommonSetup(@SuppressWarnings("unused") FMLCommonSetupEvent event) {
		registerDispensers();
	
		compositables.forEach(p -> ComposterBlock.COMPOSTABLES.put(p.getFirst().get(), (float) p.getSecond()));
	}

	public static void registerDispensers() {
		DefaultDispenseItemBehavior BOWL_BEHAVIOUR=new DefaultDispenseItemBehavior() {
			@SuppressWarnings("resource")
			@Override
			protected ItemStack execute(BlockSource bp, ItemStack is) {

				Direction d = bp.state().getValue(DispenserBlock.FACING);
				BlockPos front = bp.pos().relative(d);
				FluidState fs = bp.level().getBlockState(front).getFluidState();
				BlockEntity blockEntity = bp.level().getBlockEntity(front);
				if (blockEntity != null) {
					@Nullable ResourceHandler<FluidResource> ip=bp.level().getCapability(Capabilities.Fluid.BLOCK,front, d.getOpposite());
					if (ip!=null) {
						ItemStack ret = CauponaApi.fillBowl(is,ip).orElse(null);
						if (ret != null) {
							return consumeWithRemainder(bp,is, ret);
						}
					}
					if (blockEntity instanceof IFoodContainer pan) {
						ItemResource ir=ItemResource.of(is);
						try(Transaction trans=Transaction.openRoot()){
							ItemResource out=pan.exchangeInternal(ir, trans);
							if (ir!=out) {
								trans.commit();
								return consumeWithRemainder(bp,is, out.toStack());
							}
						}
						
					}

					return is;
				} else if (!fs.isEmpty()) {
					ItemResource ir=ItemResource.of(is);
					ContanerContainFoodEvent event=Utils.containBlock(ir, FluidResource.of(fs.getType()), 250);
					if (event.isAllowed()) {
						return consumeWithRemainder(bp,is, event.getOutput().toStack());
					}
					return is;
				}
				return super.execute(bp, is);
			}

		};
		DispenserBlock.registerBehavior(Items.BOWL, BOWL_BEHAVIOUR);
		DispenserBlock.registerBehavior(CPBlocks.LOAF_BOWL.getFirst().asItem(),BOWL_BEHAVIOUR);
		DispenserBlock.registerBehavior(CPItems.redstone_ladle.get(), new DefaultDispenseItemBehavior() {
			@SuppressWarnings("resource")
			@Override
			protected ItemStack execute(BlockSource bp, ItemStack is) {

				Direction d = bp.state().getValue(DispenserBlock.FACING);
				BlockPos front = bp.pos().relative(d);
				BlockPos back = bp.pos().relative(d.getOpposite());
				//Block src = bp.level().getBlockState(front).getBlock();
				
				@Nullable ResourceHandler<FluidResource> blockSource = bp.level().getCapability(Capabilities.Fluid.BLOCK, front,d.getOpposite());
				@Nullable ResourceHandler<FluidResource> blockTarget = bp.level().getCapability(Capabilities.Fluid.BLOCK,back, d);
				
				BlockEntity besrc=bp.level().getBlockEntity(front);
				BlockEntity betar = bp.level().getBlockEntity(back);
				try(Transaction trans=Transaction.openRoot()){
					ItemResource origItem=ItemResource.of(Items.BOWL);
					ItemResource currentItem=origItem;
					boolean isFilled=false;
					boolean succeed=false;
					if(betar instanceof IFoodContainer cont) {
						origItem=cont.getValidContainer(0);
					}
					if(!isFilled&&besrc instanceof IFoodContainer cont) {
						currentItem=cont.exchangeInternal(currentItem, trans);
						if(origItem!=currentItem) {
							isFilled=true;
						}
					}
					if(!isFilled&&blockSource!=null&&blockSource.getAmountAsInt(0)>=250) {
						try(Transaction child=Transaction.open(trans)){
							FluidResource type=blockSource.getResource(0);
							int amt=blockSource.extract(type, 250, child);
							if(amt==250) {
								ContanerContainFoodEvent event=Utils.contain(currentItem, blockSource.getResource(0), amt);
								if(event.isAllowed()) {
									child.commit();
									currentItem=event.getOutput();
									isFilled=true;
								}
							}
						}
					}
					System.out.println("isFilled: "+isFilled+",currentItem: "+currentItem);
					if(isFilled&&!currentItem.isEmpty()) {
						if(!succeed&&betar instanceof IFoodContainer cont) {
							ItemResource out=cont.exchangeInternal(currentItem, trans);
							if(out!=currentItem) {
								currentItem=out;
								succeed=true;
							}
						}
						if(!succeed&&blockTarget!=null) {
							ItemStack currentStack=currentItem.toStack();
							ItemAccess ia=new MutableStackItemAccess(currentStack);
							@Nullable ResourceHandler<FluidResource> ip = currentStack.getCapability(Capabilities.Fluid.ITEM, ia);
							if (ip!=null) {
								try(Transaction ctx=Transaction.open(trans)){
									int actual=ResourceHandlerUtil.move(ip,blockTarget, _->true, 1250, ctx);
									if (actual>0) {
										currentItem=ia.getResource();
										succeed=true;
										ctx.commit();
									}
								}
								
							}
						}
						System.out.println("isSucceed: "+succeed+",currentItem: "+currentItem);
						if(succeed&&currentItem.is(Items.BOWL)) {
							trans.commit();
						}
					}
				}
				return is;
			}

		});
		DispenserBlock.registerBehavior(CPItems.walnut_boat.get(),new BoatDispenseItemBehavior(CPEntityTypes.BOAT.get()));
		DispenserBlock.registerBehavior(CPItems.gravy_boat.get(), new DefaultDispenseItemBehavior() {
			@Override
			protected ItemStack execute(BlockSource bp, ItemStack is) {

				Direction d = bp.state().getValue(DispenserBlock.FACING);
				BlockPos front = bp.pos().relative(d);
				BlockState bs=bp.level().getBlockState(front);
				if (bs.is(CPBlocks.GRAVY_BOAT.get())) {
					int idmg = is.getDamageValue();
					is.setDamageValue(bs.getValue(GravyBoatBlock.LEVEL));
					bp.level().setBlockAndUpdate(front, bs.setValue(GravyBoatBlock.LEVEL, idmg));
					return is;
				}
				return super.execute(bp, is);
			}

		});
		DefaultDispenseItemBehavior milk = new DefaultDispenseItemBehavior() {
			@Override
			@SuppressWarnings("resource")
			public ItemStack execute(BlockSource source, ItemStack stack) {

				Direction d = source.state().getValue(DispenserBlock.FACING);
				BlockPos front = source.pos().relative(d);
				ItemAccess isr=new MutableStackItemAccess(stack);
				@Nullable ResourceHandler<FluidResource> ip = source.level().getCapability(Capabilities.Fluid.BLOCK,front, d.getOpposite());
				@Nullable ResourceHandler<FluidResource> ir = stack.getCapability(Capabilities.Fluid.ITEM,isr);
				if (ip!=null&&ir!=null) {
					try(Transaction ctx=Transaction.openRoot()){
						int actual=ResourceHandlerUtil.move(ir, ip, _->true, 1000, ctx);
			
						if (actual>0) {
							ctx.commit();
							return super.consumeWithRemainder(source, stack, isr.getResource().toStack());
						}
						return stack;
					}
				}

				return super.execute(source, stack);
			}
		};
		DispenserBlock.registerBehavior(Items.MILK_BUCKET, milk);
		DefaultDispenseItemBehavior bowlBehaviour = new DefaultDispenseItemBehavior() {
			@SuppressWarnings("resource")
			@Override
			protected ItemStack execute(BlockSource source, ItemStack stack) {
				ItemAccess isr=new MutableStackItemAccess(stack);
				ResourceHandler<FluidResource> cap=Capabilities.Fluid.ITEM.getCapability(stack,isr);
				Direction d = source.state().getValue(DispenserBlock.FACING);
				BlockPos front = source.pos().relative(d);
				BlockEntity blockEntity = source.level().getBlockEntity(front);

				if (blockEntity instanceof IFoodContainer pot) {
					ItemResource ir=ItemResource.of(stack);
					try(Transaction trans=Transaction.openRoot()){
						ItemResource out=pot.exchangeInternal(ir, trans);
						if (ir!=out) {
							trans.commit();
							return consumeWithRemainder(source,stack, out.toStack());
						}
					}
				}
				if (blockEntity != null) {
					@Nullable ResourceHandler<FluidResource> ip = source.level().getCapability(Capabilities.Fluid.BLOCK,front, d.getOpposite());
					if (ip!=null) {
						try(Transaction ctx=Transaction.openRoot()){
							int actual=ResourceHandlerUtil.move(ip,cap, _->true, 250, ctx);
				
							if (actual>0) {
								ctx.commit();
								return super.consumeWithRemainder(source, stack, isr.getResource().toStack());
							}
						}
					}
					return stack;
				}
				
				return super.execute(source, stack);
			}

		};
		for (Item i : CPItems.stews) {
			DispenserBlock.registerBehavior(i, bowlBehaviour);
		}
		DefaultDispenseItemBehavior spice = new DefaultDispenseItemBehavior() {

			@SuppressWarnings("resource")
			@Override
			protected ItemStack execute(BlockSource source, ItemStack stack) {
				Direction d = source.state().getValue(DispenserBlock.FACING);
				BlockPos front = source.pos().relative(d);
				BlockEntity blockEntity = source.level().getBlockEntity(front);

				if (blockEntity instanceof StewPotBlockEntity pot) {
					ItemResource ospice = pot.getInternInv().getResource(11);
					int num=pot.getInternInv().getAmountAsInt(11);
					pot.getInternInv().set(11, ItemResource.of(stack), 1);
					return super.consumeWithRemainder(source, stack, ospice.toStack(num));
				} else if (blockEntity instanceof PanBlockEntity pan) {
					ItemResource ospice = pan.getInternInv().getResource(11);
					int num=pan.getInternInv().getAmountAsInt(11);
					pan.getInternInv().set(11, ItemResource.of(stack),1);
					return super.consumeWithRemainder(source, stack, ospice.toStack(num));
				} else if (blockEntity instanceof CounterDoliumBlockEntity dolium) {
					ItemResource ospice = dolium.getInternInv().getResource(3);
					int num=dolium.getInternInv().getAmountAsInt(3);
					dolium.getInternInv().set(3, ItemResource.of(stack),1);
					return super.consumeWithRemainder(source, stack, ospice.toStack(num));
				}

				return super.execute(source, stack);
			}

		};
		DefaultDispenseItemBehavior pot = new DefaultDispenseItemBehavior() {
			private final DefaultDispenseItemBehavior defaultBehaviour = new DefaultDispenseItemBehavior();

			@SuppressWarnings("resource")
			@Override
			protected ItemStack execute(BlockSource source, ItemStack stack) {
				Direction d = source.state().getValue(DispenserBlock.FACING);
				BlockPos front = source.pos().relative(d);
				BlockEntity blockEntity = source.level().getBlockEntity(front);

				if (blockEntity instanceof StewPotBlockEntity pot) {
					ItemResource ospice = pot.getInternInv().getResource(11);
					int num=pot.getInternInv().getAmountAsInt(11);
					pot.getInternInv().set(11, ItemResource.EMPTY, 0);
					this.addToInventoryOrDispense(source,ospice.toStack(num));
					return stack;
				} else if (blockEntity instanceof PanBlockEntity pan) {
					ItemResource ospice = pan.getInternInv().getResource(11);
					int num=pan.getInternInv().getAmountAsInt(11);
					pan.getInternInv().set(11, ItemResource.EMPTY, 0);
					this.addToInventoryOrDispense(source,ospice.toStack(num));
					return stack;
				} else if (blockEntity instanceof CounterDoliumBlockEntity dolium) {
					ItemResource ospice = dolium.getInternInv().getResource(3);
					int num=dolium.getInternInv().getAmountAsInt(3);
					dolium.getInternInv().set(3, ItemResource.EMPTY, 0);
					this.addToInventoryOrDispense(source,ospice.toStack(num));
					return stack;
				}

				return this.defaultBehaviour.dispense(source, stack);
			}

		};
		DispenserBlock.registerBehavior(Items.FLOWER_POT, pot);
		for (DeferredHolder<Item,Item> i : CPItems.spicesItems) {
			DispenserBlock.registerBehavior(i.get(), spice);
		}
	}

}
