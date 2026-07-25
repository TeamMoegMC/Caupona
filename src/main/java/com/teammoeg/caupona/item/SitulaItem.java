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

package com.teammoeg.caupona.item;

import java.util.function.Consumer;

import com.teammoeg.caupona.util.CreativeTabItemHelper;
import com.teammoeg.caupona.util.ICreativeModeTabItem;
import com.teammoeg.caupona.util.TabType;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class SitulaItem extends Item  implements ICreativeModeTabItem{
    public SitulaItem(Properties props) {
        super(props);
    }
    public static final int MAX_CAPACITY=1250;
    @Override
	public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
    	return false;
	}

	


	@Override
	public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
		ResourceHandler<FluidResource> handler=itemStack.getCapability(Capabilities.Fluid.ITEM,ItemAccess.forStack(itemStack));
		if(handler!=null&&handler.getAmountAsLong(0)>0) {
			builder.accept(handler.getResource(0).getHoverName().copy().append(Utils.string(" "+handler.getAmountAsLong(0)+"/"+MAX_CAPACITY+" mB")));
		}
	}


	@Override
	public InteractionResult use(Level worldIn, Player playerIn, InteractionHand pUsedHand) {
		BlockHitResult ray = Item.getPlayerPOVHitResult(worldIn, playerIn, Fluid.SOURCE_ONLY);
		ItemStack cur=playerIn.getItemInHand(pUsedHand);
		if (ray.getType() == Type.BLOCK) {
			BlockPos blockpos = ray.getBlockPos();
			FluidState state = worldIn.getFluidState(blockpos);
			BlockState blk=worldIn.getBlockState(blockpos);
			
			if(state.getType()!=Fluids.EMPTY&&blk.getBlock() instanceof BucketPickup bucket) {
				try(Transaction trans=Transaction.openRoot()){
					ResourceHandler<FluidResource> handler=cur.getCapability(Capabilities.Fluid.ITEM,ItemAccess.forPlayerInteraction(playerIn, pUsedHand));
					if(handler!=null) {
						
						int amt=handler.insert(FluidResource.of(state.getType()),FluidType.BUCKET_VOLUME,trans);
						if(amt>0) {
							bucket.pickupBlock(playerIn,worldIn, blockpos, blk);
							trans.commit();
							return InteractionResult.SUCCESS;
						}
						
					}
				}
			}
			try(Transaction trans=Transaction.openRoot()){
				ResourceHandler<FluidResource> handler=cur.getCapability(Capabilities.Fluid.ITEM,ItemAccess.forPlayerInteraction(playerIn, pUsedHand));
				if(handler!=null) {
					FluidStack res=FluidUtil.tryPickupFluid(handler, playerIn, worldIn, blockpos,ray.getDirection());
					if(!res.isEmpty()) {
						trans.commit();
						return InteractionResult.SUCCESS;
					}
					
				}
			}
			
		}else if(ray.getType() == Type.MISS) {
			if(playerIn.isShiftKeyDown()) {
				try(Transaction trans=Transaction.openRoot()){
					ResourceHandler<FluidResource> handler=cur.getCapability(Capabilities.Fluid.ITEM,ItemAccess.forPlayerInteraction(playerIn, pUsedHand));
					if(handler!=null) {
						FluidResource rs=handler.getResource(0);
						int fluid=handler.extract(rs, MAX_CAPACITY, trans);
						if(fluid>0) {
							trans.commit();
							return InteractionResult.SUCCESS;
						}
					}
				}
			}
		}
		return InteractionResult.PASS;
	}

	@Override
	public void fillItemCategory(CreativeTabItemHelper helper) {
		if(helper.isType(TabType.MAIN))
			helper.accept(this);
	}

}
