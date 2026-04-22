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

package com.teammoeg.caupona.blocks.pot;

import org.jspecify.annotations.Nullable;

import com.teammoeg.caupona.CPBlockEntityTypes;
import com.teammoeg.caupona.CPCapability;
import com.teammoeg.caupona.CPConfig;
import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.api.events.ContanerContainFoodEvent;
import com.teammoeg.caupona.blocks.foods.IFoodContainer;
import com.teammoeg.caupona.blocks.stove.IStove;
import com.teammoeg.caupona.components.StewInfo;
import com.teammoeg.caupona.data.recipes.AspicMeltingRecipe;
import com.teammoeg.caupona.data.recipes.BoilingRecipe;
import com.teammoeg.caupona.data.recipes.BowlContainingRecipe;
import com.teammoeg.caupona.data.recipes.DissolveRecipe;
import com.teammoeg.caupona.data.recipes.DoliumRecipe;
import com.teammoeg.caupona.data.recipes.FoodValueRecipe;
import com.teammoeg.caupona.data.recipes.SpiceRecipe;
import com.teammoeg.caupona.data.recipes.StewCookingRecipe;
import com.teammoeg.caupona.data.recipes.StewPendingContext;
import com.teammoeg.caupona.network.CPBaseBlockEntity;
import com.teammoeg.caupona.util.IInfinitable;
import com.teammoeg.caupona.util.LazyTickWorker;
import com.teammoeg.caupona.util.LimitedInterfaceStacksHandler;
import com.teammoeg.caupona.util.MutableStackItemAccess;
import com.teammoeg.caupona.util.SpiceAddedResourceHandler;
import com.teammoeg.caupona.util.TwoSlotItemAccess;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.RangedResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class StewPotBlockEntity extends CPBaseBlockEntity implements MenuProvider, IInfinitable,IFoodContainer {
	private ItemStacksResourceHandler internInv = new ItemStacksResourceHandler(12) {

		@Override
		protected void onContentsChanged(int index, ItemStack previousContents) {
			syncData();
			super.onContentsChanged(index, previousContents);
		}

	};
	private LimitedInterfaceStacksHandler inv = new LimitedInterfaceStacksHandler(internInv) {
		@Override
		public boolean isValid(int slot, ItemResource stack) {
			if (slot < 9)
				return stack.getItem() == Items.POTION || StewCookingRecipe.isCookable(stack.toStack());
			if (slot == 9) {
				return BowlContainingRecipe.isBowl(stack.toStack()) || !Utils.getFluidType(stack.toStack()).isEmpty() || AspicMeltingRecipe.find(stack) != null;
			}
			if (slot == 11)
				return SpiceRecipe.isValid(stack.toStack());
			return false;
		}

		@Override
		public long getCapacityAsLong(int slot, ItemResource stack) {
			if (slot < 9)
				return 1;
			return super.getCapacityAsLong(slot,stack);
		}
	};
	public LimitedInterfaceStacksHandler getInv() {
		return inv;
	}

	private FluidStacksResourceHandler tank = new FluidStacksResourceHandler(1,1250) {
		@Override
		protected void onContentsChanged(int index, FluidStack previousContents){
			resetStillCounter();
			syncData();
		}

	};

	public final SpiceAddedResourceHandler modtank=new SpiceAddedResourceHandler(tank,internInv,11);
	public StewPotBlockEntity(BlockPos p, BlockState s) {
		super(CPBlockEntityTypes.STEW_POT.get(), p, s);
		still=new LazyTickWorker(()->{
			if (inv.getResource(10).isEmpty()) {
				FluidStack fs=tank.getResource(0).toStack(tank.getAmountAsInt(0));
				DoliumRecipe recipe = DoliumRecipe.testPot(fs);
				if (recipe != null) {
					still.rewind(10);
					if(recipe.handle(tank,internInv,10).resetsProcess())
						resetStillCounter();
				}
				
			}
			
			return true;
		});
		contain=new LazyTickWorker(CPConfig.SERVER.containerTick.get(),()->{
			if (canAddFluid()) {
				if(tryContianFluid())
					return true;
			}
			
			return false;
		});
	}
	private boolean tryContianFluid() {
		ItemResource container=internInv.getResource(9);
		if(!container.isEmpty()) {
			try(Transaction trans=Transaction.openRoot()){
				if (!isInfinite) {
					RecipeHolder<AspicMeltingRecipe> amr = AspicMeltingRecipe.find(container);
					if (amr != null) {
						ItemStack aspicStack=container.toStack();
						FluidStack fs = amr.value().handle(aspicStack);
						FluidResource fr=FluidResource.of(fs);
						int remainSpace = tank.getCapacityAsInt(0, fr) - tank.getAmountAsInt(0);
						int produce = Math.min(remainSpace / amr.value().amount, internInv.getAmountAsInt(9));
						int toInsert=produce*fs.getAmount();
						if (internInv.extract(9,container, produce, trans)==produce) {
							if(tryAddFluid(fr,toInsert,amr.value().time, false,trans)) {
								ItemStackTemplate ist=aspicStack.getCraftingRemainder();
								if(ist!=null) {
									ItemStack ret = ist.create();
									int toReturn=ret.count()*produce;
									if(internInv.insert(10,internInv.getResourceFrom(ret), toReturn, trans)!=toReturn) {
										return false;
									}
									
								}
								still.stop();
								trans.commit();
								return true;
								
							}
						}
					}else {
						ItemStack containerStack=container.toStack();
						@Nullable ResourceHandler<FluidResource> cap=containerStack.getCapability(Capabilities.Fluid.ITEM,new TwoSlotItemAccess(internInv, 9,10));
						if(cap!=null) {
							FluidResource fr=cap.getResource(0);
							int amt=cap.extract(fr, cap.getAmountAsInt(0), trans);
							if (tryAddFluid(fr,amt,trans)) {
								trans.commit();
								return true;
							}
						}
					
					}
				}
			}
			try(Transaction trans=Transaction.openRoot()){
				if(modtank.getAmountAsInt(0)>=250) {
					FluidResource rs=modtank.getResource(0);
					int itemCount=internInv.extract(9, container, 1, trans);
					int fluidAmount=modtank.extract(rs, 250, trans);
					if(itemCount>0&&fluidAmount>=250) {
						ContanerContainFoodEvent result=Utils.contain(container,rs,fluidAmount);
						if(result.isAllowed()) {
							if(internInv.insert(10,result.getOutput(), 1, trans)==1) {
								trans.commit();
								return true;
							}
						}
					}
				}
			}
			
		}
		return false;
	}
	public ResourceHandler<FluidResource> getTank() {
		return tank;
	}

	//Process
	public int process;
	public int processMax;

	public LazyTickWorker still;

	public LazyTickWorker contain;

	//stores working properties
	public boolean working = false;
	public boolean operate = false;
	public short proctype = 0;
	public boolean rsstate = false;
	boolean mayBeStill=true;
	
	boolean isInfinite = false;

	//stores Result
	public FluidStack output;
	public void resetResult() {
		output=null;
	}
	public static final short NOP = 0;
	public static final short BOILING = 1;
	public static final short COOKING = 2;
	public static final short STIRING = 3;
	public void resetStillCounter() {
		mayBeStill=true;
		still.stop();
	}
	@Override
	public void tick() {
		boolean syncNeeded=false;
		if (!level.isClientSide()) {
			working = false;
			if (processMax > 0) {
				resetStillCounter();
				if (level.getCapability(CPCapability.HEAT_STOVE, worldPosition.below(), Direction.UP) instanceof IStove stove) {
					try(Transaction trans=Transaction.openRoot()){
						int rh = stove.requestHeat(processMax-process,trans);
						trans.commit();
						if (!isInfinite) {
							process += rh;
							if(rh>0)
								syncNeeded=true;
						}
						if (rh > 0)
							working = true;
						if (process >= processMax) {
							process = 0;
							processMax = 0;
							doWork();
							syncNeeded=true;
						}
					}
				} else
					return;

			} else {
				if (!tank.getResource(0).isEmpty() && !isInfinite) {
					syncNeeded|=still.tick();
					if(!still.isRunning()&&mayBeStill) {
						DoliumRecipe rcp=DoliumRecipe.testPot(tank.getResource(0).toStack(tank.getAmountAsInt(0)));
						if(rcp!=null) {
							still.start(rcp.time);
						}else mayBeStill=false;
					}
				}
				if (!isInfinite&&proctype<=1) {
					prepareWork();
					if(proctype!=0)
						syncNeeded=true;
				}
				syncNeeded|=contain.tick();
			}
			if(syncNeeded)
				this.syncData();
		}
		
		
	}

	public boolean canAddFluid() {
		return proctype == 0;
	}

	@Override
	public void readCustomNBT(ValueInput nbt, boolean isClient) {
		process = nbt.getIntOr("process",0);
		processMax = nbt.getIntOr("processMax",0);
		proctype = (short) nbt.getShortOr("worktype",(short) 0);
		rsstate = nbt.getBooleanOr("rsstate",false);
		if (isClient)
			working = nbt.getBooleanOr("working",false);
		tank.deserialize(nbt);
		output=nbt.read("output", FluidStack.CODEC).orElse(null);
		isInfinite = nbt.getBooleanOr("inf",false);
		if (!isClient) {
			internInv.deserialize(nbt.childOrEmpty("inv"));
			still.read(nbt,"nowork");
		}
	}

	@Override
	public void writeCustomNBT(ValueOutput nbt, boolean isClient) {
		nbt.putInt("process", process);
		nbt.putInt("processMax", processMax);
		nbt.putShort("worktype", proctype);
		nbt.putBoolean("rsstate", rsstate);
		if (isClient)
			nbt.putBoolean("working", working);

		tank.serialize(nbt);;
		if(output!=null)
			nbt.store("output", FluidStack.CODEC, output);
		
		nbt.putBoolean("inf", isInfinite);
		if (!isClient) {
			internInv.serialize(nbt.child("inv"));
			still.write(nbt,"nowork");
		}
	}

	private void prepareWork() {
		if (rsstate&&proctype==0&& !operate && level.hasNeighborSignal(this.worldPosition))
			operate = true;

		if (operate&&proctype==0) {
			operate = false;
			if (!(level.getCapability(CPCapability.HEAT_STOVE, worldPosition.below(), Direction.UP) instanceof IStove stove) || !stove.canEmitHeat())
				return;
			if (doBoil())
				proctype = 1;
			else if (makeSoup())
				proctype = 2;
		} else if (proctype == 1) {
			if (makeSoup())
				proctype = 2;
			else {
				proctype = 0;
				this.syncData();
			}
		}
	}

	private void doWork() {
		if(output!=null) {
			FluidStack in=output;
			output=null;
			this.tank.set(0, FluidResource.of(in), in.getAmount());
			
			if (proctype == 1) {
				boolean hasItem = false;
				for (int i = 0; i < 9; i++) {
					ItemResource is = inv.getResource(i);
					if (!is.isEmpty()) {
						hasItem = true;
						break;
					}
				}
				if (!hasItem)
					proctype = 0;
			}else proctype = 0;
		}else proctype = 0;
	}

	private boolean doBoil() {
		FluidStack stack=this.tank.getResource(0).toStack(tank.getAmountAsInt(0));
		RecipeHolder<BoilingRecipe> recipeh = BoilingRecipe.recipes.stream().filter(t->t.value().matches(stack)).findFirst().orElse(null);
		if (recipeh == null)
			return false;
		BoilingRecipe recipe=recipeh.value();
		output=recipe.handle(stack);
		this.processMax = (int) (recipe.time * (tank.getAmountAsInt(0) / 250f));
		this.process = 0;
		
		return true;
	}


	private boolean makeSoup() {
		//System.out.println("1");
		if (tank.getAmountAsInt(0) <= 250)
			return false;// can't boil if under one bowl
		try(Transaction trans=Transaction.openRoot()){
			FluidResource originType=tank.getResource(0);
			StewInfo currentInfo=Utils.getOrCreateInfoForRead(originType).copy();
			//System.out.println("2");
			if (currentInfo.getStacks().size() > 27)
				return false;// too much ingredients
			int oparts = tank.getAmountAsInt(0) / 250;
			int parts = oparts - 1;

			//System.out.println("3");
			int tpt = CPConfig.SERVER.potMixTimeBase.get();
			for (int i = 0; i < 9; i++) {
				ItemResource is = internInv.getResource(i);
				if (!is.isEmpty()) {
					if(internInv.extract(i, is, 1, trans)==1) {
						if (is.getItem() == Items.POTION) {
							for (MobEffectInstance eff : is.get(DataComponents.POTION_CONTENTS).getAllEffects())
								currentInfo.addEffect(eff, parts);
							if(internInv.insert(i, ItemResource.of(Items.GLASS_BOTTLE), 1, trans)!=1)
								return false;
						} else if (StewCookingRecipe.isCookable(is)) {
							ItemStack toput=is.toStack();
							for (RecipeHolder<DissolveRecipe> rs : DissolveRecipe.recipes) {
								if (rs.value().item.test(toput)) {
									tpt += rs.value().time;
									break;
								}
							}
							FoodValueRecipe fvr = FoodValueRecipe.recipes.get(is.getItem());
							if (fvr != null)
								tpt += fvr.processtimes.getOrDefault(is.getItem(), 0);
							currentInfo.addItem(toput, oparts);
							ItemStackTemplate ist=toput.getCraftingRemainder();
							if(ist!=null) {
								ItemStack reminder=ist.create();
								if(internInv.insert(i, ItemResource.of(reminder), reminder.getCount(), trans)!=reminder.getCount())
									return false;
							}
						}else
							return false;
					}
				}
			}
			currentInfo.adjustParts(oparts, parts);
			//System.out.println("4:"+itms+"/"+parts+"/"+cr.size()+"/"+currentInfo.getDensity());
			if (currentInfo.getDensity() > 3 || currentInfo.getPotionEffects().size() > 3) {// too dense
				return false;
			}
			//System.out.println("5");
			process = 0;

			//System.out.println("6");
			//System.out.println("7");
			currentInfo.completeAll();
			tpt = Math.max(CPConfig.SERVER.potCookTimeBase.get(), tpt);
			
			
			processMax = Math.max(outputResult(originType.toStack(parts*250),currentInfo), tpt);
			trans.commit();
			return true;
		}
	}
	
	private int outputResult(FluidStack original,StewInfo currentInfo) {

		Fluid become = original.getFluid();
		
		StewPendingContext ctx = new StewPendingContext(currentInfo, become);
		Fluid nextbase = become;
		if (ctx.getItems().isEmpty()) {
			return 0;
		}
		
		for (RecipeHolder<StewCookingRecipe> cr : StewCookingRecipe.sorted) {
			int mt = cr.value().matches(ctx);
			//System.out.println(cr.id()+":"+mt);
			if (mt != 0) {
				if (mt == 2)
					nextbase = become;
				else
					nextbase = currentInfo.getBase();
				become = cr.value().output;
				original=new FluidStack(become,original.getAmount());
				if(!cr.value().removeNBT) {
					currentInfo.setBase(nextbase);
					currentInfo.recalculateHAS();
					Utils.setInfo(original, currentInfo);
				}
				output=original;
				
				return cr.value().time;
			}
		}
		currentInfo.recalculateHAS();
		Utils.setInfo(original, currentInfo);
		output=original;
		return 0;
	}

	public boolean tryAddFluid(FluidResource fs,int amount, Transaction root) {
		return tryAddFluid(fs,amount, CPConfig.SERVER.potMixTimeBase.get(), true,root);
	}

	public boolean tryAddFluid(FluidResource fs,int amount, int extraTime, boolean canIgnoreHeat,Transaction root) {
		if (isInfinite)
			return false;
		if(proctype!=0)
			return false;
		try(Transaction trans=Transaction.open(root)){
			if (canIgnoreHeat) {
				if (tank.insert(fs, amount, trans) == amount) {
					trans.commit();
					return true;
				}
			} else {
				int tryFill=tank.insert(fs, amount, trans);
				if (tryFill==amount) {
					this.proctype = 3;
					this.process = 0;
					this.processMax = extraTime;
					resetResult();
					trans.commit();
					return true;
				}
			}
		}
		try(Transaction trans=Transaction.open(root)){
			if (tank.getCapacityAsInt(0,fs) - tank.getAmountAsInt(0) < amount)
				return false;
			if (!(level.getCapability(CPCapability.HEAT_STOVE, worldPosition.below(), Direction.UP) instanceof IStove stove) || !stove.canEmitHeat())
				return false;
			StewInfo n = Utils.getOrCreateInfo(fs);
			int pm = 0;
			StewInfo currentInfo=Utils.getOrCreateInfo(tank.getResource(0));
			if (currentInfo.getBase()!=n.getBase() && currentInfo.getBase()!=fs.getFluid()
					&& n.getBase()!=tank.getResource(0).getFluid()) {
				FluidStack fst=fs.toStack(amount);
				RecipeHolder<BoilingRecipe> bnx = BoilingRecipe.recipes.stream().filter(t->t.value().matches(fst)).findFirst().orElse(null);
				if (bnx == null)
					return false;
				if (currentInfo.getBase()!=bnx.value().after)
					return false;
				n = Utils.getOrCreateInfo(FluidResource.of(bnx.value().handle(fst)));
				pm = (int) (bnx.value().time * (amount / 250f));
			}
	
			if (currentInfo.merge(n, tank.getAmountAsInt(0) / 250f, amount / 250f)) {
				int oamount = tank.getAmountAsInt(0);
				int namount = oamount+amount;
				currentInfo.adjustParts(oamount / 250,namount / 250);
				int num = Math.max(outputResult(tank.getResource(0).toStack((namount / 250)*250),currentInfo), extraTime);
				this.proctype = 3;
				this.process = 0;
				this.processMax = Math.max(pm, num);
	
				trans.commit();
				return true;
				
			}
	
			return false;
		}
	}

	@Override
	public AbstractContainerMenu createMenu(int p1, Inventory p2, Player p3) {
		return new StewPotContainer(p1, p2, this);
	}

	@Override
	public Component getDisplayName() {
		return Utils.translate("container." + CPMain.MODID + ".stewpot.title");
	}

	@Override
	public void handleMessage(short type, int data) {
		if (type == 0)
			if (this.proctype == 0)
				this.operate = true;
		if (type == 1) {
			if (data == 1)
				rsstate = false;
			else if (data == 2)
				rsstate = true;
		}
		this.syncData();

	}

	public RangedResourceHandler<ItemResource> bowl = new RangedResourceHandler<>(inv,9,12) {


		@Override
		public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
			if(index==1)
				return 0;
			return super.insert(index, resource, amount, transaction);
		}

		@Override
		public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
			if(index==0||index==2)
				return 0;
			return super.extract(index, resource, amount, transaction);
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

	@Override
	public Object getCapability(BlockCapability<?, Direction> cap, Direction side) {
		if (cap == Capabilities.Item.BLOCK) {
			if (side == Direction.UP)
				return ingredient;
			return this.bowl;
		}
		if (cap == Capabilities.Fluid.BLOCK)
			return modtank;
		return null;
	}



	@Override
	public boolean setInfinity() {
		return isInfinite = !isInfinite;
	}
	@Override
	public boolean isInfinite() {
		return isInfinite;
	}
	public ItemStacksResourceHandler getInternInv() {
		return internInv;
	}
	@Override
	public ItemResource exchangeInternal(int num, ItemResource is, TransactionContext parent) {
		
		ItemStack stack=is.toStack();
		ItemAccess ia=new MutableStackItemAccess(stack);
		@Nullable ResourceHandler<FluidResource> cap=stack.getCapability(Capabilities.Fluid.ITEM,ia);
		if(cap!=null) {
			try(Transaction trans=Transaction.open(parent)){
				FluidResource fr=cap.getResource(0);
				int amt=cap.extract(fr, cap.getAmountAsInt(0), trans);
				if (tryAddFluid(fr,amt,trans)) {
					trans.commit();
					ItemResource ir=ia.getResource();
					System.out.println(ir);
					return ir;
				}
			}
		}
		
		if(modtank.getAmountAsInt(0)>=250) {
			try(Transaction trans=Transaction.open(parent)){
				
				FluidResource rs=modtank.getResource(0);
				int fluidAmount=modtank.extract(rs, 250, trans);
				if(fluidAmount>=250) {
					ContanerContainFoodEvent result=Utils.contain(is,rs,fluidAmount);
					if(result.isAllowed()) {
						trans.commit();
						ItemResource ir=result.getOutput();
						return ir;
					}
					
				}
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
		ItemStack it=is.toStack();
		return BowlContainingRecipe.isBowl(it) || !Utils.getFluidType(it).isEmpty();
	}
}
