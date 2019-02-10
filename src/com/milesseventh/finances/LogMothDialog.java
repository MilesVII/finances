package com.milesseventh.finances;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

public class LogMothDialog extends DialogFragment {
	private final LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	private final LayoutParams fieldlp = new LayoutParams(0, LayoutParams.MATCH_PARENT, .5f);
	public class DeltaEntryView extends LinearLayout{
		public static final int DELTA_FIELD_ID = 77200;
		public static final int NAME_FIELD_ID = 77201;
		
		public DeltaEntryView(Context context) {
			super(context);
		}

		public int retrieveValue(){
			EditText et = (EditText)findViewById(DELTA_FIELD_ID);
			if (et != null && !et.getText().toString().isEmpty())
				return Integer.parseInt(et.getText().toString());
			else
				return -1;
		}
		
		public String retrieveName(){
			EditText et = (EditText)findViewById(NAME_FIELD_ID);
			if (et != null)
				return et.getText().toString();
			else
				return null;
		}
	}
	
	public Moth moth = null;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		Builder builder = new Builder(getActivity());

		final ScrollView sv = new ScrollView(getContext());
		sv.setLayoutParams(lp);
		
		final LinearLayout layout = new LinearLayout(getContext());
		layout.setLayoutParams(lp);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setPadding(7, 7, 7, 7);
		
		sv.addView(layout);
		
		final TextView tv = new TextView(getContext());
		String details = String.format("Income: %d\nExpenses: %s\nEfficiency: %.2f%%\nPrevious balance: %d",
		                               moth.sum(moth.cleanIncome), moth.processUndef(moth.expenses), 
		                               moth.getEfficiency(), moth.previousBalance);
		tv.setText(details);
		layout.addView(tv);
		
		final EditText balanceField = new EditText(getContext());
		balanceField.setLayoutParams(lp);
		balanceField.setHint("Current balance");
		balanceField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_VARIATION_NORMAL);
		balanceField.setText("" + moth.balance);
		layout.addView(balanceField);
		
		/////////////////////////////////////////////////////////////////////////
		//Clean income
		
		final TextView tClearIncome = new TextView(getContext());
		tClearIncome.setText("Clear income:");
		layout.addView(tClearIncome);
		
		final LinearLayout subLayoutCI = new LinearLayout(getContext());
		subLayoutCI.setLayoutParams(lp);
		subLayoutCI.setOrientation(LinearLayout.VERTICAL);
		layout.addView(subLayoutCI);
		
		Button bAddIncome = new Button(getContext());
		bAddIncome.setText("Add clear income");
		bAddIncome.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				subLayoutCI.addView(generateEntry("Income source", subLayoutCI, null));
			}
		});
		for (Delta d: moth.cleanIncome)
			subLayoutCI.addView(generateEntry("Income source", subLayoutCI, d));
		layout.addView(bAddIncome);
		
		/////////////////////////////////////////////////////////////////////////
		//Unregistered income
		
		final TextView tUnregisteredIncome = new TextView(getContext());
		tUnregisteredIncome.setText("Unstable income:");
		layout.addView(tUnregisteredIncome);
		
		final LinearLayout subLayoutUI = new LinearLayout(getContext());
		subLayoutUI.setLayoutParams(lp);
		subLayoutUI.setOrientation(LinearLayout.VERTICAL);
		layout.addView(subLayoutUI);
		
		Button bAddUnregIncome = new Button(getContext());
		bAddUnregIncome.setText("Add unregistered income");
		bAddUnregIncome.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				subLayoutUI.addView(generateEntry("Income source", subLayoutUI, null));
			}
		});
		for (Delta d: moth.unregisteredIncome)
			subLayoutUI.addView(generateEntry("Income source", subLayoutUI, d));
		layout.addView(bAddUnregIncome);
		
		/////////////////////////////////////////////////////////////////////////
		//Loans
		
		final TextView tLoans = new TextView(getContext());
		tLoans.setText("Loans and investments:");
		layout.addView(tLoans);
		
		final LinearLayout subLayoutL = new LinearLayout(getContext());
		subLayoutL.setLayoutParams(lp);
		subLayoutL.setOrientation(LinearLayout.VERTICAL);
		layout.addView(subLayoutL);
		
		Button bAddLoan = new Button(getContext());
		bAddLoan.setText("Add loan or investment");
		bAddLoan.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				subLayoutL.addView(generateEntry("Borrower", subLayoutL, null));
			}
		});
		for (Delta d: moth.spentOnLoans)
			subLayoutL.addView(generateEntry("Borrower", subLayoutL, d));
		layout.addView(bAddLoan);
		
		builder.setView(sv).setTitle(moth.name).setNegativeButton("Cancel", new OnClickListener(){
			@Override
			public void onClick(DialogInterface unused, int unusedtoo) {
				
			}
		}).setPositiveButton("Submit", new OnClickListener(){
			@Override
			public void onClick(DialogInterface unused, int unusedtoo) {
				if (balanceField.getText().length() == 0)
					return;
				
				moth.cleanIncome.clear();
				moth.unregisteredIncome.clear();
				moth.spentOnLoans.clear();
				moth.balance = Integer.parseInt(balanceField.getText().toString());
				for (int i = 0; i < subLayoutCI.getChildCount(); ++i){
					DeltaEntryView dev = (DeltaEntryView)subLayoutCI.getChildAt(i);
					if (dev.retrieveValue() != -1)
						moth.cleanIncome.add(new Delta(dev.retrieveName(), dev.retrieveValue()));
				}//TODO: bad
				for (int i = 0; i < subLayoutUI.getChildCount(); ++i){
					DeltaEntryView dev = (DeltaEntryView)subLayoutUI.getChildAt(i);
					if (dev.retrieveValue() != -1)
						moth.unregisteredIncome.add(new Delta(dev.retrieveName(), dev.retrieveValue()));
				}
				for (int i = 0; i < subLayoutL.getChildCount(); ++i){
					DeltaEntryView dev = (DeltaEntryView)subLayoutL.getChildAt(i);
					if (dev.retrieveValue() != -1)
						moth.spentOnLoans.add(new Delta(dev.retrieveName(), dev.retrieveValue()));
				}
				moth.calculate();
				
				if (!MainActivity.antistatic.mothRegistered(moth))
					MainActivity.antistatic.registerMoth(moth);
				
				MainActivity.antistatic.sync();
			}
		});
		return builder.create();
	}

	private DeltaEntryView generateEntry(String name, final LinearLayout parent, Delta delta){
		final DeltaEntryView layout = new DeltaEntryView(getContext());
		layout.setLayoutParams(lp);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		
		EditText nameField = new EditText(getContext());
		nameField.setLayoutParams(fieldlp);
		nameField.setHint(name);
		nameField.setId(DeltaEntryView.NAME_FIELD_ID);
		
		EditText deltaField = new EditText(getContext());
		deltaField.setLayoutParams(fieldlp);
		deltaField.setHint(name);
		deltaField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_VARIATION_NORMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
		deltaField.setId(DeltaEntryView.DELTA_FIELD_ID);
		
		if (delta != null){
			nameField.setText(delta.comment);
			deltaField.setText("" + delta.delta);
		}
		
		Button bDelete = new Button(getContext());
		bDelete.setText("X");
		bDelete.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				parent.removeView(layout);
			}
		});

		layout.addView(nameField);
		layout.addView(deltaField);
		layout.addView(bDelete);
		
		return layout;
	}
}
