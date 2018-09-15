package com.milesseventh.finances;

public class Account {
	public enum Marker {
		SAVING, RETURNABLE
	}
	
	public Account(String nname, Marker nmarker){
		name = nname;
		marker = nmarker;
	}
	
	public String name;
	public Marker marker;
	public int balance = 0;
}