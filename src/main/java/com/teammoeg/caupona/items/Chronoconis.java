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

package com.teammoeg.caupona.items;

import java.util.List;

import com.teammoeg.caupona.util.IInfinitable;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class Chronoconis extends CPItem {

	public Chronoconis(String name, Properties properties) {
		super(name, properties);
	}

	@Override
	public void fillItemCategory(CreativeModeTab pCategory, NonNullList<ItemStack> pItems) {

	}

	@SuppressWarnings("resource")
	@Override
	public InteractionResult useOn(UseOnContext pContext) {
		InteractionResult def = super.useOn(pContext);
		if (!pContext.getLevel().isClientSide) {
			if (pContext.getLevel().getBlockEntity(pContext.getClickedPos()) instanceof IInfinitable inf) {
				pContext.getPlayer().sendSystemMessage(
						Utils.translate("message.caupona.chronoconis", inf.setInfinity()));

				return InteractionResult.SUCCESS;
			}
		}
		return def;
	}

	@Override
	public void appendHoverText(ItemStack pStack, Level pLevel, List<Component> pTooltipComponents,
			TooltipFlag pIsAdvanced) {
		pTooltipComponents.add(Utils.translate("tooltip.caupona.chronoconis"));
		super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);

	}

}
