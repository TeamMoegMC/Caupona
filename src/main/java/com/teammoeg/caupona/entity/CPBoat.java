package com.teammoeg.caupona.entity;

import com.teammoeg.caupona.CPEntityTypes;
import com.teammoeg.caupona.Main;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;

public class CPBoat extends Boat {
	private static final EntityDataAccessor<String> WOOD_TYPE = SynchedEntityData.defineId(CPBoat.class,
			EntityDataSerializers.STRING);

	public CPBoat(EntityType<? extends Boat> p_38290_, Level p_38291_) {
		super(p_38290_, p_38291_);
	}

	public CPBoat(Level p_38293_, double p_38294_, double p_38295_, double p_38296_) {
		this(CPEntityTypes.BOAT.get(), p_38293_);
		this.setPos(p_38294_, p_38295_, p_38296_);
		this.xo = p_38294_;
		this.yo = p_38295_;
		this.zo = p_38296_;
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(WOOD_TYPE, "walnut");
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		this.setWoodType(compound.getString("CPType"));
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("CPType", this.getWoodType());
	}

	public String getWoodType() {
		return this.entityData.get(WOOD_TYPE);
	}

	public void setWoodType(String wood) {
		this.entityData.set(WOOD_TYPE, wood);
	}

	@Override
	public Item getDropItem() {
		return ForgeRegistries.ITEMS.getValue(new ResourceLocation(Main.MODID, getWoodType() + "_boat"));
	}

	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

}
