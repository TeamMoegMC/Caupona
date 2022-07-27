package com.teammoeg.caupona.items;

import java.util.List;

import com.teammoeg.caupona.util.IInfinitable;

import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class Chronoconis extends CPItem {

	public Chronoconis(String name, Properties properties) {
		super(name, properties);
	}

	@Override
	public void fillItemCategory(CreativeModeTab pCategory, NonNullList<ItemStack> pItems) {
		
	}

	@Override
	public InteractionResult useOn(UseOnContext pContext) {
		InteractionResult def= super.useOn(pContext);
		if(!pContext.getLevel().isClientSide) {
			BlockEntity te=pContext.getLevel().getBlockEntity(pContext.getClickedPos());
			if(te instanceof IInfinitable) {
				pContext.getPlayer().sendMessage(new TranslatableComponent("message.caupona.chronoconis",((IInfinitable) te).setInfinity()),null);
				
				return InteractionResult.SUCCESS;
			}
		}
		return def;
	}

	@Override
	public void appendHoverText(ItemStack pStack, Level pLevel, List<Component> pTooltipComponents,
			TooltipFlag pIsAdvanced) {
		pTooltipComponents.add(new TranslatableComponent("tooltip.caupona.chronoconis"));
		super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
		
	}

}
