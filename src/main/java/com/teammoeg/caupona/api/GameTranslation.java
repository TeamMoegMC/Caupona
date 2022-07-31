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

package com.teammoeg.caupona.api;

import com.teammoeg.caupona.data.TranslationProvider;

import net.minecraft.network.chat.TranslatableComponent;

public class GameTranslation implements TranslationProvider {
	private static GameTranslation INSTANCE;

	private GameTranslation() {
	}

	public static TranslationProvider get() {
		if (INSTANCE == null)
			INSTANCE = new GameTranslation();
		return INSTANCE;
	}

	@Override
	public String getTranslation(String key, Object... objects) {
		return new TranslatableComponent(key, objects).getString();
	}

}
