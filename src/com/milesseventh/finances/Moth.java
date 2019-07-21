package com.milesseventh.finances;

import java.io.Serializable;
import java.util.ArrayList;

public class Moth implements Serializable {
	private static final long serialVersionUID = 6809989950961664203L;
	
	public ArrayList<Delta> cleanIncome = new ArrayList<Delta>();
	public ArrayList<Delta> unregisteredIncome = new ArrayList<Delta>();

	public int balance = -1, previousBalance = -1;
	public int capitalInput = -1; //Calculated automatically
	
	public int expenses = -1; //Calculated automatically
	public ArrayList<Delta> spentOnLoans = new ArrayList<Delta>();
	
	public String name = "NONAME";
	
	public Moth() {}
	
	public void calculate(){
		if (balance == -1)
			return;
		
		Moth m = MainActivity.antistatic.requestPreviousMoth(this);
		previousBalance = m == null ? 0 : m.balance;
		
		expenses = (sum(cleanIncome) + sum(unregisteredIncome)) - (balance - previousBalance);
	}

	public int getNetIncome(){
		return sum(cleanIncome) + sum(unregisteredIncome) - (expenses - -sum(spentOnLoans));
	}
	
	public static float getEfficiency(int balance, int previousBalance, int spentOnLoans, int unregisteredIncome, int cleanIncome){
		//return 1f - (expenses - spentOnLoans) / (float)cleanIncome;
		return (balance - previousBalance + spentOnLoans - unregisteredIncome) / (float)cleanIncome;
	}
	
	public float getEfficiency(){
		return getEfficiency(balance, previousBalance, sum(spentOnLoans), sum(unregisteredIncome), sum(cleanIncome));
		//return 1f - (expenses - sum(spentOnLoans)) / (float)sum(cleanIncome);
	}

	public String processUndef(int in){
		if (in == -1)
			return "N/A";
		else 
			return "" + in; 
	}
	
	public int sum(ArrayList<Delta> deltas){
		if (deltas.size() == 0)
			return 0;
		
		int sum = 0;
		for (Delta d: deltas)
			sum += d.delta;
		return sum;
	}
}
