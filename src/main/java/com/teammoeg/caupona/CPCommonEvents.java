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

import java.util.Optional;
import org.jetbrains.annotations.Nullable;

import com.teammoeg.caupona.api.CauponaApi;
import com.teammoeg.caupona.api.events.ContanerContainFoodEvent;
import com.teammoeg.caupona.api.events.EventResult;
import com.teammoeg.caupona.api.events.FoodExchangeItemEvent;
import com.teammoeg.caupona.components.StewInfo;
import com.teammoeg.caupona.data.recipes.AspicMeltingRecipe;
import com.teammoeg.caupona.data.recipes.BoilingRecipe;
import com.teammoeg.caupona.data.recipes.BowlContainingRecipe;
import com.teammoeg.caupona.data.recipes.DoliumRecipe;
import com.teammoeg.caupona.data.recipes.SauteedRecipe;
import com.teammoeg.caupona.data.recipes.SpiceRecipe;
import com.teammoeg.caupona.data.recipes.StewCookingRecipe;
import com.teammoeg.caupona.util.ITickableContainer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.ConsumableListener;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

@EventBusSubscriber
public class CPCommonEvents {
	/*@SubscribeEvent
	public static void addReloadListeners(AddServerReloadListenersEvent event) {
		event.addListener(CPMain.rl("reloadrecipe"),new RecipeReloadListener(event.getServerResources()));
	}*/

	@SubscribeEvent
	public static void isExtractAllowed(FoodExchangeItemEvent.Pre event) {
		if(!BowlContainingRecipe.isBowl(event.getOrigin()))
			event.setResult(EventResult.ALLOW);
	}
	@SubscribeEvent
	public static void isExchangeAllowed(FoodExchangeItemEvent.Post event) {
		if(!BowlContainingRecipe.isBowl(event.getOrigin())&&BowlContainingRecipe.isBowl(event.getTarget()))
			event.setResult(EventResult.ALLOW);
	}
	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent.Pre event) {
		if (event.getEntity().containerMenu instanceof ITickableContainer container)
			container.tick(event.getEntity() instanceof ServerPlayer);
	}
	@SubscribeEvent
	public static void bowlContainerFood(ContanerContainFoodEvent ev) {
		if(ev.getInputFluidAmount()!=250)
			return;
		FluidStack testStack=ev.createStack();
		RecipeHolder<BowlContainingRecipe> recipe = BowlContainingRecipe.getRecipes(ev.createInputStack()).stream().filter(t->t.value().matches(testStack)).findFirst().orElse(null);
		if (recipe != null) {
			ev.setOutput(recipe.value().handle(ev.getInputFluid()));
		}
	}
	@SubscribeEvent
	public static void addManualToPlayer(OnDatapackSyncEvent event) {
		event.sendRecipes(BowlContainingRecipe.TYPE.get(),
				BoilingRecipe.TYPE.get(),
				StewCookingRecipe.TYPE.get(),
				SauteedRecipe.TYPE.get(),
				DoliumRecipe.TYPE.get(),
				AspicMeltingRecipe.TYPE.get(),
				SpiceRecipe.TYPE.get()
				);

	}
	
	/**
	 * @param event  
	 */
	@SubscribeEvent
	public static void addManualToPlayer(PlayerEvent.PlayerLoggedInEvent event) {
/*
		if(!CPConfig.SERVER.addManual.get() || !ModList.get().isLoaded("patchouli"))return;
		CompoundTag nbt = event.getEntity().getPersistentData();
		CompoundTag persistent;

		if (nbt.contains(Player.PERSISTED_NBT_TAG)) {
			persistent = nbt.getCompound(Player.PERSISTED_NBT_TAG);
		} else {
			nbt.put(Player.PERSISTED_NBT_TAG, (persistent = new CompoundTag()));
		}
		if (!persistent.contains(CPMain.BOOK_NBT_TAG)) {
			persistent.putBoolean(CPMain.BOOK_NBT_TAG,true);
			ItemHandlerHelper.giveItemToPlayer(event.getEntity(),PatchouliAPI.get().getBookStack(Identifier.fromNamespaceAndPath(CPMain.MODID,"book")));
		}*/
	}
	@SuppressWarnings("resource")
	@SubscribeEvent
	public static void onBlockClick(PlayerInteractEvent.RightClickBlock event) {
		if(event.getLevel().isClientSide())return;//Workaround for https://github.com/TeamMoegMC/Caupona/issues/107
		ItemStack is = event.getItemStack();
		if(is.isEmpty())return;
		Player playerIn = event.getEntity();
		Level worldIn = event.getLevel();
		BlockPos blockpos = event.getPos();
		BlockEntity blockEntity = worldIn.getBlockEntity(blockpos);
		if (blockEntity != null) {
			ResourceHandler<FluidResource> handler=worldIn.getCapability(Capabilities.Fluid.BLOCK, blockpos, event.getFace());
			if(handler!=null){
				Optional<ItemStack> out=CauponaApi.getFilledItemStack(handler,is);
				if(out.isPresent()) {
					ItemStack ret = out.get();
					event.setCanceled(true);
					event.setCancellationResult(worldIn.isClientSide()?InteractionResult.SUCCESS:InteractionResult.SUCCESS_SERVER);
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
					event.setCancellationResult(worldIn.isClientSide()?InteractionResult.SUCCESS:InteractionResult.SUCCESS_SERVER);
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
		if (event.getEntity() != null && !event.getEntity().level().isClientSide()
				&& event.getEntity() instanceof ServerPlayer) {
			ItemStack stack = event.getItemStack();
			@Nullable ResourceHandler<FluidResource> cap = stack
					.getCapability(Capabilities.Fluid.ITEM,ItemAccess.forPlayerInteraction(event.getEntity(), event.getHand()));
			if (cap!=null && stack.is(CPTags.Items.CONTAINER)) {
				StewInfo si = cap.getResource(0).get(CPCapability.STEW_INFO);
				if (si!=null&&!event.getEntity().canEat(si.canAlwaysEat())) {
					event.setCancellationResult(InteractionResult.FAIL);
					event.setCanceled(true);
				}

			}
		}
	}

	@SubscribeEvent
	public static void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
		if (event.getEntity() != null
				&& event.getEntity() instanceof ServerPlayer serverPlayer) {
			ItemStack stack = event.getItem();
			@Nullable ResourceHandler<FluidResource> cap = stack
				.getCapability(Capabilities.Fluid.ITEM,ItemAccess.forPlayerInteraction(serverPlayer, event.getHand()));
			if (cap!=null && stack.is(CPTags.Items.CONTAINER)) {
				FluidResource fr=cap.getResource(0);
				Consumable si = fr.get(DataComponents.CONSUMABLE);
				if(si!=null) {
					Level level=serverPlayer.level();
			        RandomSource random = serverPlayer.getRandom();
			        si.emitParticlesAndSounds(random, serverPlayer, stack, 4);
			        fr.getAllOfType(ConsumableListener.class).forEach(component -> 
			        component.onConsume(level, serverPlayer, stack, si));
			        si.onConsumeEffects().forEach(action -> action.apply(level, stack, serverPlayer));
			        serverPlayer.gameEvent(si.animation() == ItemUseAnimation.DRINK ? GameEvent.DRINK : GameEvent.EAT);
				}
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
