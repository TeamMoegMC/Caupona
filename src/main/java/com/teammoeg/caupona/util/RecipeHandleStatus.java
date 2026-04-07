package com.teammoeg.caupona.util;

public enum RecipeHandleStatus {
	SUCCEED,BLOCKED,FAILED;
	public static RecipeHandleStatus success(boolean isSuccess) {
		return isSuccess?SUCCEED:BLOCKED;
	}
	public boolean resetsProcess() {
		return this==SUCCEED||this==FAILED;
	}
}
