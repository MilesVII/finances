package com.milesseventh.finances;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
	private LinearLayout operationsList;
	private Button newOperation;
	private TextView fundsAvailable;
	private ArrayList<Operation> operations;
	public ArrayList<SavingAccount> accounts = new ArrayList<SavingAccount>();
	public static MainActivity antistatic;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		antistatic = this;
		setContentView(R.layout.activity_main);
		
		fundsAvailable = (TextView)findViewById(R.id.funds_available);
		
		newOperation = (Button)findViewById(R.id.add_operation);
		newOperation.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View unused) {
				LogOperationDialog lod = new LogOperationDialog();
				lod.show(getFragmentManager(), "");
			}
		});
		
		operationsList = (LinearLayout)findViewById(R.id.main_list);
		operations = Utils.load(this, null);
		syncOperations();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
		case (R.id.mm_month):
			break;
		case (R.id.mm_export):
			export();
			break;
		case (R.id.mm_specialtags):
			Utils.shout("Use #save:<name> tag to transfer digits to your saving account\n"
			          + "#use:<name>:<price> when buying goods using before-saved money\n"
			          + "#kill:<name> to liquidate saving account and make it's savings available");
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public String[] getListOfAllUsedTags(){
		ArrayList<String> tags = new ArrayList<String>();
		for (Operation o: operations)
			for (String tag: o.tags)
				if (!Utils.contains(tags, tag))
					tags.add(tag);
		String[] out = new String[tags.size()];
		return tags.toArray(out);
	}

	public void syncOperations(){
		//Render operation history
		operationsList.removeAllViews();
		accounts.clear();
		for (int i = operations.size() - 1; i >= 0; --i){
			operationsList.addView(new OperationView(this, operations.get(i)));
		}
		
		//Process savings balance
		int balance = 0, total = 0;;
		SavingAccount greedy;
		
		for (Operation horsey: operations){
			switch(horsey.getType()){
			case DEFAULT:
				balance += horsey.delta;
				total += horsey.delta;
				break;
			case SAVE:
				greedy = Utils.findAccount(accounts, horsey.tagarg1);
				if (greedy == null){
					greedy = new SavingAccount(horsey.tagarg1);
					accounts.add(greedy);
				}
				greedy.balance -= horsey.delta;
				break;
			case USE:
				greedy = Utils.findAccount(accounts, horsey.tagarg1);
				if (greedy != null){
					balance += greedy.balance - horsey.tagargnum;
					total -= horsey.tagargnum;
					accounts.remove(greedy);
				}
				break;
			case KILL:
				greedy = Utils.findAccount(accounts, horsey.tagarg1);
				if (greedy != null){
					balance += greedy.balance;
					accounts.remove(greedy);
				}
				break;
			default:
				break;
			}
		}
		
		////////////////////////////////////
		StringBuilder sb = new StringBuilder("Available: ");
		sb.append(balance);
		sb.append(" (");
		sb.append(total);
		sb.append(" on card)\nSaving Accounts:");
		for (SavingAccount sa: accounts){
			sb.append('\n');
			sb.append(sa.name);
			sb.append(": ");
			sb.append(sa.balance);
		}
		fundsAvailable.setText(sb.toString());
	}
	
	public void add(Operation operation){
		operations.add(operation);
		syncOperations();
		Utils.save(this, operations, null);
	}	
	
	public void replace(Operation source, Operation noperation){
		int knife = operations.indexOf(source);
		operations.remove(source);
		operations.add(knife, noperation);
		syncOperations();
		Utils.save(this, operations, null);
	}
	
	public void remove(Operation operation){
		operations.remove(operation);
		syncOperations();
		Utils.save(this, operations, null);
	}
	
	public void export(){
		StringBuilder sb = new StringBuilder();
		for (Operation whitehors: operations){
			sb.append(Integer.toString(whitehors.delta));
			sb.append('\n');
			sb.append(whitehors.comment);
			sb.append('\n');
			for (String tag: whitehors.tags){
				sb.append(tag);
				sb.append('\n');
			}
			sb.append('\n');
		}
		Utils.copy(this, sb.toString());
	}
}
