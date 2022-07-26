package com.teammoeg.caupona.items;

import net.minecraft.core.NonNullList;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class EdibleBlock extends CPBlockItem {

	public EdibleBlock(Block block, Properties props) {
		super(block, props);
	}

	public EdibleBlock(Block block, Properties props, String name) {
		super(block, props, name);
	}
	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
		if (this.allowdedIn(group)) {
			items.add(new ItemStack(this));
		}
	}
	/**
	 * Returns the unlocalized name of this item.
	 */
	public String getDescriptionId() {
		return this.getOrCreateDescriptionId();
	}

	public ItemStack finishUsingItem(ItemStack itemstack, Level worldIn, LivingEntity entityLiving) {
		super.finishUsingItem(itemstack, worldIn, entityLiving);
		return new ItemStack(Items.BOWL);
	}

	/**
	 * Called when this item is used when targetting a Block
	 */
	public InteractionResult useOn(UseOnContext pContext) {
		InteractionResult interactionresult = InteractionResult.PASS;
		if (pContext.getPlayer().isShiftKeyDown())
			interactionresult = this.place(new BlockPlaceContext(pContext));
		//if(!pContext.getPlayer().getCooldowns().isOnCooldown(CPItems.water))
			if (!interactionresult.consumesAction() && this.isEdible()) {
				
				InteractionResult interactionresult1 = this
						.use(pContext.getLevel(), pContext.getPlayer(), pContext.getHand()).getResult();
				return interactionresult1 == InteractionResult.CONSUME ? InteractionResult.CONSUME_PARTIAL
						: interactionresult1;
			}
		return interactionresult;
	}

}