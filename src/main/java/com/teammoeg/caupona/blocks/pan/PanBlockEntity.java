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

package com.teammoeg.caupona.blocks.pan;

import javax.annotation.Nullable;

import com.teammoeg.caupona.CPBlockEntityTypes;
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPCapability;
import com.teammoeg.caupona.CPConfig;
import com.teammoeg.caupona.CPItems;
import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.blocks.foods.IFoodContainer;
import com.teammoeg.caupona.blocks.stove.IStove;
import com.teammoeg.caupona.components.SauteedFoodInfo;
import com.teammoeg.caupona.data.recipes.FoodValueRecipe;
import com.teammoeg.caupona.data.recipes.PanPendingContext;
import com.teammoeg.caupona.data.recipes.SauteedRecipe;
import com.teammoeg.caupona.data.recipes.SpiceRecipe;
import com.teammoeg.caupona.network.CPBaseBlockEntity;
import com.teammoeg.caupona.util.IInfinitable;
import com.teammoeg.caupona.util.LimitedInterfaceStacksHandler;
import com.teammoeg.caupona.util.RecipeHandleStatus;
import com.teammoeg.caupona.util.RecipeHandler;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.RangedResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class PanBlockEntity extends CPBaseBlockEntity implements MenuProvider,IInfinitable,IFoodContainer {

	//work state
	public boolean working = false;
	public boolean operate = false;
	public boolean rsstate = false;
	
	boolean isInfinite = false;
	public ItemStack sout = ItemStack.EMPTY;
	public Identifier model;
	public static final int ACCESSIBLE_SLOTS=12;
	public RecipeHandler<SauteedRecipe> handler=new RecipeHandler<>(this::make);
	//Capabilities
	public ItemStacksResourceHandler internInv = new ItemStacksResourceHandler(ACCESSIBLE_SLOTS) {

		@Override
		protected void onContentsChanged(int index, ItemStack previousContents) {
			syncData();
			super.onContentsChanged(index, previousContents);
		}
	};
	private LimitedInterfaceStacksHandler inv=new LimitedInterfaceStacksHandler(internInv) {
		@Override
		public boolean isValid(int slot, ItemResource stack) {
			if (slot < 9)
				return SauteedRecipe.isCookable(stack.toStack());
			if (slot == 9) {
				return SauteedRecipe.isBowl(stack.toStack());
			}
			if (slot == 11)
				return SpiceRecipe.isValid(stack.toStack());
			return false;
		}

		@Override
		public long getCapacityAsLong(int slot,ItemResource ir) {
			if (slot < 9)
				return 1;
			return super.getCapacityAsLong(slot, ir);
		}

	};
	public RangedResourceHandler<ItemResource> bowl = new RangedResourceHandler<>(inv,9,12) {
		
		@Override
		public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
			if(index==1)
				return 0;
			return super.insert(index, resource, amount, transaction);
		}

		@Override
		public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
			if(index==1)
				return super.extract(index, resource, amount, transaction);
			return 0;
		}

	};
	RangedResourceHandler<ItemResource> ingredient = new RangedResourceHandler<>(inv, 0, 10) {

		@Override
		public int extract(ItemResource resource, int amount, TransactionContext transaction) {
			return 0;
		}

		@Override
		public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
			return 0;
		}

		
	};
	public PanBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
		super(CPBlockEntityTypes.PAN.get(), pWorldPosition, pBlockState);
	}

	@Override
	public void handleMessage(short type, int data) {
		if (type == 0)
			this.operate = true;
		if (type == 1) {
			if (data == 1)
				rsstate = false;
			else if (data == 2)
				rsstate = true;
		}

	}

	@Override
	public void readCustomNBT(ValueInput nbt, boolean isClient) {
		working = nbt.getBooleanOr("working",false);
		operate = nbt.getBooleanOr("operate",false);
		rsstate = nbt.getBooleanOr("rsstate",false);
		model=nbt.getString("model").map(Identifier::parse).orElse(null);
		if (!isClient) {
			sout=nbt.read("sout", ItemStack.CODEC).orElse(ItemStack.EMPTY);
			internInv.deserialize(nbt.childOrEmpty("items"));
			isInfinite =nbt.getBooleanOr("inf",false);
		}
		

	}

	@Override
	public void writeCustomNBT(ValueOutput nbt, boolean isClient) {
		nbt.putBoolean("working", working);
		nbt.putBoolean("operate", operate);
		nbt.putBoolean("rsstate", rsstate);
		if(model!=null)
		nbt.putString("model", model.toString());
		if (!isClient) {
			if(!sout.isEmpty())
				nbt.store("sout", ItemStack.CODEC, sout);
			internInv.serialize(nbt.child("items"));
			nbt.putBoolean("inf",isInfinite);
		}
		
		
	}

	private ItemStack tryAddSpice(ItemStack fs,TransactionContext trans) {
		ItemResource ospi = internInv.getResource(11);
		ItemStack spi=ospi.toStack();
		SpiceRecipe spice = SpiceRecipe.find(spi);
		if(this.getBlockState().is(CPBlocks.LEAD_PAN.get())) {
			if(spice!=null&&spice.canReactLead) {
				spi=CPItems.getSapa();
				spice=SpiceRecipe.find(spi);
			}
		}
		if (spice != null && SpiceRecipe.getMaxUse(spi) >= fs.getCount()) {
			if(fs.get(CPCapability.SAUTEED_INFO.get()) instanceof SauteedFoodInfo si) {
				if (!isInfinite) {
					if(internInv.extract(11,ospi, 1, trans)==1) {
						spi=SpiceRecipe.handle(spi, fs.getCount());
						if(internInv.insert(11,internInv.getResourceFrom(spi), spi.getCount(), trans)==spi.getCount()) {
							si=si.copy();
							si.addSpice(spice.effect, ospi.getItem());
							fs=fs.copy();
							fs.set(CPCapability.SAUTEED_INFO.get(), si);
							return fs;
						}
					}
				}
			}
		}
		return fs;
	}

	@Override
	public void tick() {
		if (!level.isClientSide()) {
			working = false;
			if (handler.shouldTick()) {
				if (level.getCapability(CPCapability.HEAT_STOVE, worldPosition.below(), Direction.UP) instanceof IStove stove) {
					if(!handler.isRecipeFinished()) {
						int rh =0;
						try(Transaction trans=Transaction.openRoot()){
							rh=stove.requestHeat(2, trans);
							trans.commit();
						}
							
						if(handler.tickProcess(rh)) {
							
							working = true;
							this.syncData();
						}
						
					}
				} else
					return;

			} else if (!sout.isEmpty()) {
				operate = false;
				try(Transaction trans=Transaction.openRoot()) {
					ItemResource ir=internInv.getResourceFrom(tryAddSpice(sout.copyWithCount(1),trans));
					if(internInv.insert(10,ir, 1, trans)==1) {
						if(!isInfinite)
							sout.shrink(1);
						trans.commit();
					}
					
					this.setChanged();
					if(sout.isEmpty()) {
						sout=ItemStack.EMPTY;
						model=null;
						this.syncData();
					}
				}
			} else {
				prepareWork();
			}
		}
		
	}

	private void prepareWork() {
		if (rsstate && !operate && level.hasNeighborSignal(this.worldPosition))
			operate = true;

		if (operate) {
			operate = false;
			if (!(level.getCapability(CPCapability.HEAT_STOVE, worldPosition.below(), Direction.UP)  instanceof IStove stove) || !stove.canEmitHeat())
				return;
			make(null);
		}
	}


	@SuppressWarnings("resource")
	private RecipeHandleStatus make(@Nullable Identifier recipeId) {
		//Do simulation requirement check
		//Ensure everything cookable
		try(Transaction trans=Transaction.openRoot()){
			ItemStacksResourceHandler tempInv = new ItemStacksResourceHandler(9);
			int itms = 0;
			for (int i = 0; i < 9; i++) {
				ItemResource ir=internInv.getResource(i);
				if(!ir.isEmpty()) {
					int extracted=internInv.extract(i, ir, 1, trans);
					
					if (extracted>0) {
						ItemStack in=ir.toStack(extracted);
						if (SauteedRecipe.isCookable(in)) {
							
							if(tempInv.insert(ir, 1, trans)==1) {
								ItemStackTemplate ist=in.getCraftingRemainder();
								if(ist!=null) {
									ItemStack reminder=ist.create();
									if(internInv.insert(internInv.getResourceFrom(reminder), reminder.getCount(), trans)!=reminder.getCount()) {
										return RecipeHandleStatus.FAILED;
									}
								}
								itms++;
								continue;
							}
						}
						return RecipeHandleStatus.FAILED;
					}
				}
			}
			if (itms <= 0)
				return RecipeHandleStatus.FAILED;
			//ensure has oil
			BlockPos oilProvidingPos=null;
			for (Direction d : Utils.horizontals) {
				BlockPos bp = this.getBlockPos().relative(d);
				BlockState bs = this.getLevel().getBlockState(bp);
				if (bs.is(CPBlocks.GRAVY_BOAT.get())) {
					int oil = GravyBoatBlock.getOil(bs);
					if (oil > 0) {
						oilProvidingPos=bp;
						break;
					}
				}
			}
			if (oilProvidingPos==null)
				return RecipeHandleStatus.FAILED;
			if (internInv.getResource(9).isEmpty())
				return RecipeHandleStatus.FAILED;
	
			//Make Pending Context
			int tpt = 0;
			SauteedFoodInfo current = new SauteedFoodInfo();
			for (int i = 0; i < 9; i++) {
				ItemResource is = tempInv.getResource(i);
				if (is.isEmpty())
					break;
				current.addItem(is.toStack(tempInv.getAmountAsInt(i)));
				FoodValueRecipe fvr = FoodValueRecipe.recipes.get(is.getItem());
				if (fvr != null)
					tpt += fvr.processtimes.getOrDefault(is.getItem(), 0);
			}
			current.completeAll();
			
			PanPendingContext ctx = new PanPendingContext(current);
			//Do recipe check
			float tcount=0;
			
			ItemStackTemplate preout=null;
			Identifier tmodel = null;
			boolean removesNBT=false;
			ItemResource bowl=internInv.getResource(9);
			RecipeHolder<SauteedRecipe> recipe=null;
			for (RecipeHolder<SauteedRecipe> cr : SauteedRecipe.sorted) {
				if ((recipeId==null||cr.id().identifier().equals(recipeId))&&cr.value().bowl.test(bowl.toStack())&&cr.value().matches(ctx)) {
					tpt = Math.max(cr.value().time, tpt);
					preout = cr.value().output;
					removesNBT=cr.value().removeNBT;
					tcount=cr.value().count;
					tmodel=cr.value().model;
					recipe=cr;
					break;
				}
			}
			if(preout==null)
				return RecipeHandleStatus.FAILED;
			if(tcount<=0)tcount=2f;
			int cook = Mth.ceil(itms / tcount);
			if (internInv.getAmountAsInt(9) < cook)
				return RecipeHandleStatus.FAILED;
			if(recipeId!=null) {
				//Complete simulation check, Start taking effect
				GravyBoatBlock.drawOil(getLevel(), oilProvidingPos, 1);
				current.setParts(cook);
				current.recalculateHAS();
				ItemStack sout=preout.create();
				sout.setCount(sout.getCount()*cook);
				if(!removesNBT)
					Utils.setInfo(sout,current);
				this.sout=sout;
				this.model=tmodel;
				internInv.extract(9, bowl, cook, trans);
				trans.commit();
				return RecipeHandleStatus.SUCCEED;
			}else {
				tpt = Math.max(CPConfig.SERVER.fryTimeBase.get(), tpt);
				if (this.getBlockState().is(CPBlocks.STONE_PAN.get()))
					tpt *= 2;
				handler.setRecipe(recipe, tpt);
			}
			return RecipeHandleStatus.SUCCEED;
		}
	}

	@Override
	public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
		return new PanContainer(pContainerId, pInventory, this);
	}

	@Override
	public Component getDisplayName() {
		return Utils.translate("container." + CPMain.MODID + ".pan.title");
	}

	public LimitedInterfaceStacksHandler getInv() {
		return inv;
	}
	public ItemStacksResourceHandler getInternInv() {
		return internInv;
	}
	@Override
	public boolean setInfinity() {
		return isInfinite=!isInfinite;
	}


	@Override
	public ItemResource exchangeInternal(int num, ItemResource is,TransactionContext parent) {
		ItemResource ir=internInv.getResource(10);
		try(Transaction trans=Transaction.open(parent)){
			int outCount=1;
			if(!is.isEmpty()) {
				outCount=internInv.insert(9,is,1,trans);
			}
			if(outCount==1) {
				int extracted=internInv.extract(10, ir, 1, trans);
				trans.commit();
				if(extracted==1) {
					return ir;
				}
				return ItemResource.EMPTY;
			}
		}
		return is;
	}

	@Override
	public int getSlots() {
		return 1;
	}

	@Override
	public boolean accepts(int num, ItemResource is) {
		return SauteedRecipe.isBowl(is.toStack());
	}

	@Override
	public Object getCapability(BlockCapability<?, Direction> type, Direction d) {
		if(type==Capabilities.Item.BLOCK) {
			if(d==Direction.UP)
				return ingredient;
			return bowl;
		}
		return null;
	}
	@Override
	public boolean isInfinite() {
		return isInfinite;
	}

	@Override
	public ItemResource getValidContainer(int slot) {
		ItemResource ir=internInv.getResource(9);
		if(!ir.isEmpty())
			return ir;
		return ItemResource.of(Items.BOWL);
	}
}
