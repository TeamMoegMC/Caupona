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

import java.util.Optional;

import com.teammoeg.caupona.api.CauponaApi;
import com.teammoeg.caupona.api.events.ContanerContainFoodEvent;
import com.teammoeg.caupona.api.events.FoodExchangeItemEvent;
import com.teammoeg.caupona.data.RecipeReloadListener;
import com.teammoeg.caupona.data.recipes.BowlContainingRecipe;
import com.teammoeg.caupona.fluid.SoupFluid;
import com.teammoeg.caupona.util.ITickableContainer;
import com.teammoeg.caupona.util.StewInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemHandlerHelper;
import vazkii.patchouli.api.PatchouliAPI;

@Mod.EventBusSubscriber
public class CPCommonEvents {
	@SubscribeEvent
	public static void addReloadListeners(AddReloadListenerEvent event) {
		event.addListener(new RecipeReloadListener(event.getServerResources()));
	}

	@SubscribeEvent
	public static void isExtractAllowed(FoodExchangeItemEvent.Pre event) {
		if(!event.getOrigin().is(Items.BOWL))
			event.setResult(Result.ALLOW);
	}
	@SubscribeEvent
	public static void isExchangeAllowed(FoodExchangeItemEvent.Post event) {
		if((!event.getOrigin().is(Items.BOWL))&&event.getTarget().is(Items.BOWL))
			event.setResult(Result.ALLOW);
	}
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase == Phase.START) {
			if (event.player.containerMenu instanceof ITickableContainer container) 
				container.tick(event.side == LogicalSide.SERVER);
		}
	}
	@SubscribeEvent
	public static void bowlContainerFood(ContanerContainFoodEvent ev) {
		if(ev.origin.getItem()==Items.BOWL) {
			BowlContainingRecipe recipe=BowlContainingRecipe.recipes.get(ev.fs.getFluid());
			if(recipe!=null) {
				ev.out=recipe.handle(ev.fs);
				ev.setResult(Result.ALLOW);
			}
		}
	}
	@SubscribeEvent
	public static void addManualToPlayer(PlayerEvent.PlayerLoggedInEvent event) {
		
		if(!CPConfig.SERVER.addManual.get())return;
		if(!ModList.get().isLoaded("patchouli"))return;
		CompoundTag nbt = event.getEntity().getPersistentData();
		CompoundTag persistent;

		if (nbt.contains(Player.PERSISTED_NBT_TAG)) {
			persistent = nbt.getCompound(Player.PERSISTED_NBT_TAG);
		} else {
			nbt.put(Player.PERSISTED_NBT_TAG, (persistent = new CompoundTag()));
		}
		if (!persistent.contains(CPMain.BOOK_NBT_TAG)) {
			persistent.putBoolean(CPMain.BOOK_NBT_TAG,true);
			ItemHandlerHelper.giveItemToPlayer(event.getEntity(),PatchouliAPI.get().getBookStack(new ResourceLocation(CPMain.MODID,"book")));
		}
	}
	@SuppressWarnings("resource")
	@SubscribeEvent
	public static void onBlockClick(PlayerInteractEvent.RightClickBlock event) {
		ItemStack is = event.getItemStack();
		Player playerIn = event.getEntity();
		Level worldIn = event.getLevel();
		BlockPos blockpos = event.getPos();
		BlockEntity blockEntity = worldIn.getBlockEntity(blockpos);
		if (blockEntity != null) {
			blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER, event.getFace()).ifPresent(handler -> {
				Optional<ItemStack> out=CauponaApi.getFilledItemStack(handler,is);
				if(out.isPresent()) {
					ItemStack ret = out.get();
					event.setCanceled(true);
					event.setCancellationResult(InteractionResult.sidedSuccess(worldIn.isClientSide));
					if (is.getCount() > 1) {
						is.shrink(1);
						if (!playerIn.addItem(ret)) {
							playerIn.drop(ret, false);
						}
					} else
						playerIn.setItemInHand(event.getHand(), ret);
				}
			});
		}

	}

	@SuppressWarnings("resource")
	@SubscribeEvent
	public static void onItemUse(PlayerInteractEvent.RightClickItem event) {
		ItemStack is = event.getItemStack();
		Level worldIn = event.getLevel();
		Player playerIn = event.getEntity();
		BlockHitResult ray = Item.getPlayerPOVHitResult(worldIn, playerIn, Fluid.SOURCE_ONLY);
		if (ray.getType() == Type.BLOCK) {
			BlockPos blockpos = ray.getBlockPos();
			BlockState blockstate1 = worldIn.getBlockState(blockpos);
			net.minecraft.world.level.material.Fluid f = blockstate1.getFluidState().getType();
			if (f != Fluids.EMPTY) {
				Optional<ItemStack> out=CauponaApi.getBlockFilledItemStack(f, is);
				if(out.isPresent()) {
					ItemStack ret = out.get();
					event.setCanceled(true);
					event.setCancellationResult(InteractionResult.sidedSuccess(worldIn.isClientSide));
					if (is.getCount() > 1) {
						is.shrink(1);
						if (!playerIn.addItem(ret)) {
							playerIn.drop(ret, false);
						}
					} else
						playerIn.setItemInHand(event.getHand(), ret);
				}
			}
		}
		
	}

	@SubscribeEvent
	public static void onBowlUse(PlayerInteractEvent.RightClickItem event) {
		if (event.getEntity() != null && !event.getEntity().level().isClientSide
				&& event.getEntity() instanceof ServerPlayer) {
			ItemStack stack = event.getItemStack();
			LazyOptional<IFluidHandlerItem> cap = stack
					.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
			if (cap.isPresent() && stack.is(CPTags.Items.CONTAINER)) {
				IFluidHandlerItem data = cap.resolve().get();
				if (data.getFluidInTank(0).getFluid() instanceof SoupFluid) {
					StewInfo si = SoupFluid.getInfo(data.getFluidInTank(0));
					if (!event.getEntity().canEat(si.canAlwaysEat())) {
						event.setCancellationResult(InteractionResult.FAIL);
						event.setCanceled(true);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
		if (event.getEntity() != null && !event.getEntity().level().isClientSide
				&& event.getEntity() instanceof ServerPlayer) {
			ItemStack stack = event.getItem();
			LazyOptional<IFluidHandlerItem> cap = stack
					.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
			if (cap.isPresent() && stack.is(CPTags.Items.CONTAINER)) {
				IFluidHandlerItem data = cap.resolve().get();
				if (data.getFluidInTank(0).getFluid() instanceof SoupFluid)
					CauponaApi.apply(event.getEntity().level(), event.getEntity(),
							SoupFluid.getInfo(data.getFluidInTank(0)));
			}
		}
	}
/*
	@SubscribeEvent(priority = EventPriority.LOW)
	public static void addFeatures(BiomeLoadingEvent event) {
		if (event.getName() != null) {
			BiomeCategory category = event.getCategory();
			// WALNUT
			if (category != BiomeCategory.NETHER && category != BiomeCategory.THEEND) {
				if (Config.SERVER.genWalnut.get() && category == BiomeCategory.FOREST) {
					event.getGeneration().addFeature(Decoration.VEGETAL_DECORATION, CPPlacements.TREES_WALNUT);
				}
				if (Config.SERVER.genFig.get())
					if (category == BiomeCategory.PLAINS || category == BiomeCategory.SAVANNA) {
						event.getGeneration().addFeature(Decoration.VEGETAL_DECORATION, CPPlacements.TREES_FIG);
					}
				if (Config.SERVER.genWolfberry.get())
					if (category == BiomeCategory.EXTREME_HILLS) {
						event.getGeneration().addFeature(Decoration.VEGETAL_DECORATION, CPPlacements.TREES_WOLFBERRY);
					}

			}
			// Structures

		}
	}*/
}
