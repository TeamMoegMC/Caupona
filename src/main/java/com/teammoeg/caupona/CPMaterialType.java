package com.teammoeg.caupona;

public class CPMaterialType {
	String name;

	public CPMaterialType(String name) {
		super();
		this.name = name;
	}
	boolean hasDeco;
	public CPMaterialType hasDecoration(){
		this.hasDeco=true;
		return this;
	}
	int counterGrade;
	public CPMaterialType hasCounter(int grade){
		this.counterGrade=grade;
		return this;
	}
	boolean hasPill;
	public CPMaterialType hasPillar(){
		this.hasPill=true;
		return this;
	}
	boolean hasHypo;
	public CPMaterialType hasHypocaust(){
		this.hasHypo=true;
		return this;
	}
	public String getName() {
		return name;
	}
	public boolean isHasPill() {
		return hasPill;
	}
	public boolean isHasDeco() {
		return hasDeco;
	}
	public int getCounterGrade() {
		return counterGrade;
	}
	public boolean isHasHypo() {
		return hasHypo;
	}
}
