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
 * You should have received a copy of the GNU General Public License
 * along with Caupona. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.caupona;

import java.util.ArrayList;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class Config {

	public static void register() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);
	}

	public static class Client {
		/**
		 * @param builder
		 */
		Client(ForgeConfigSpec.Builder builder) {
		}
	}

	public static class Common {
		/**
		 * @param builder
		 */
		public ConfigValue<Integer> cooldown;
		
		Common(ForgeConfigSpec.Builder builder) {
			builder.push("Stews");
			
			cooldown=builder.comment("Add Cooldown to all soups per saturation added").define("CooldownTicksPerSaturation",10);
			builder.pop();
		}
	}

	public static class Server {
		/**
		 * @param builder
		 */
		public ConfigValue<Integer> chimneyTicks;
		public ConfigValue<Integer> chimneyCheck;
		public ConfigValue<Integer> chimneyStorage;
		public ConfigValue<Integer> stoveCD;
		public ConfigValue<Integer> fumaroleSpeed;
		public ConfigValue<Integer> fumaroleCheck;
		public ConfigValue<Integer> fumarolePower;
		public ConfigValue<Float> stoveFuel;
		public ConfigValue<Boolean> genWalnut;
		public ConfigValue<Boolean> genWolfberry;
		public ConfigValue<Boolean> genFig;
		Server(ForgeConfigSpec.Builder builder) {
			builder.push("chimney");
			chimneyTicks=builder.comment("How many ticks does a chimney pot needed to make a soot").define("ChimneySootTicks",80);
			chimneyCheck=builder.comment("How many ticks does a chimney check it's validity").defineInRange("ChimneyCheckTicks",20, 1, 2000);
			chimneyStorage=builder.comment("Max soot stored in a chimney").defineInRange("ChimneySootStorage",8, 1,64);
			builder.pop();
			builder.push("stoves");
			stoveCD=builder.comment("How many ticks should the stove pause burning when work is done").define("StovePauseTimer",100);
			stoveFuel=builder.comment("Stove fuel value multiplier").define("StoveFuelMultiplier",1.0f);
			builder.pop();
			builder.push("fumarole");
			fumaroleSpeed=builder.comment("How many tick does fumarole vent need to generate pumice bloom").defineInRange("FumaroleTicks",100,1,2000);
			fumaroleCheck=builder.comment("How many ticks should the fumarole check its heat source").defineInRange("FumaroleCheckTicks",20,1,200);
			fumarolePower=builder.comment("Fumarole heat value, set to 0 to disable fumarole heat.").defineInRange("FumaroleHeat",1,0,10);
			builder.push("worldgen");
			genWalnut=builder.comment("Generate Walnut trees").define("generateWalnut",true);
			genWolfberry=builder.comment("Generate Wolfberry trees").define("generateWolfberry",true);
			genFig=builder.comment("Fig Walnut trees").define("generateFig",true);
			builder.pop();
		}
	}

	public static final ForgeConfigSpec CLIENT_CONFIG;
	public static final ForgeConfigSpec COMMON_CONFIG;
	public static final ForgeConfigSpec SERVER_CONFIG;
	public static final Client CLIENT;
	public static final Common COMMON;
	public static final Server SERVER;

	public static ArrayList<String> DEFAULT_WHITELIST = new ArrayList<>();

	static {
		ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
		CLIENT = new Client(CLIENT_BUILDER);
		CLIENT_CONFIG = CLIENT_BUILDER.build();
		ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
		COMMON = new Common(COMMON_BUILDER);
		COMMON_CONFIG = COMMON_BUILDER.build();
		ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
		SERVER = new Server(SERVER_BUILDER);
		SERVER_CONFIG = SERVER_BUILDER.build();
	}
}
