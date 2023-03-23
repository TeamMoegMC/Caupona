package com.teammoeg.caupona.util;

public class MaterialType {
	String name;

	public MaterialType(String name) {
		super();
		this.name = name;
	}
	private boolean hasDeco;
	public MaterialType makeDecoration(){
		this.hasDeco=true;
		return this;
	}
	private int counterGrade;
	public MaterialType makeCounter(int grade){
		this.counterGrade=grade;
		return this;
	}
	private boolean hasPill;
	public MaterialType makePillar(){
		this.hasPill=true;
		return this;
	}
	private boolean hasHypo;
	public MaterialType makeHypocaust(){
		this.hasHypo=true;
		return this;
	}
	public String getName() {
		return name;
	}
	public boolean isPillarMaterial() {
		return hasPill;
	}
	public boolean isDecorationMaterial() {
		return hasDeco;
	}
	public int getCounterGrade() {
		return counterGrade;
	}
	public boolean isCounterMaterial() {
		return counterGrade!=0;
	}
	public boolean isHypocaustMaterial() {
		return hasHypo;
	}
}
