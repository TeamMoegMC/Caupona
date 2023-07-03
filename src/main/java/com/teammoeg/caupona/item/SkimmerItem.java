package com.teammoeg.caupona.item;

import com.teammoeg.caupona.CPItems;
import com.teammoeg.caupona.blocks.pot.StewPotBlockEntity;
import com.teammoeg.caupona.fluid.SoupFluid;
import com.teammoeg.caupona.util.FloatemStack;
import com.teammoeg.caupona.util.StewInfo;
import com.teammoeg.caupona.util.TabType;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemHandlerHelper;

public class SkimmerItem extends CPItem {

	public SkimmerItem(Properties properties) {
		super(properties, TabType.MAIN);
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
		 BlockPos pos=context.getClickedPos();
		 BlockEntity be=context.getLevel().getBlockEntity(pos);
		 if(be instanceof StewPotBlockEntity stewpot) {
			 if(stewpot.canAddFluid()) {
				 FluidTank tank=stewpot.getTank();
				 FluidStack fluid=tank.getFluidInTank(0);
				 StewInfo si=SoupFluid.getInfo(fluid);
				 float dense=si.getDensity();
				 if(dense>0.5) {
					 float toreduce=Math.min(dense-0.5f,0.5f);
					 float reduced=toreduce*fluid.getAmount()/250f;
					 for(FloatemStack lstack:si.stacks) {
						 lstack.shrink(lstack.getCount()/dense*toreduce);
					 }
					 si.recalculateHAS();
					 SoupFluid.setInfo(fluid, si);
					 tank.setFluid(fluid);
					 stack.hurtAndBreak( 1,context.getPlayer(),(p_289501_) -> {
	                     p_289501_.broadcastBreakEvent(context.getPlayer().getUsedItemHand());
	                  });
					 float frac=Mth.frac(reduced);
					 int amt=Mth.floor(reduced);
					 if(context.getPlayer().getRandom().nextFloat()<frac)
						 amt++;
					 ItemHandlerHelper.giveItemToPlayer(context.getPlayer(), new ItemStack(CPItems.scraps.get(),amt));
					 return InteractionResult.SUCCESS;
				 }
				 
			 }
			 return InteractionResult.FAIL;
		 }
		 return InteractionResult.PASS;
	}


}
