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

	private OnClickListener logOperationButton = new OnClickListener(){
			@Override
			public void onClick(View unused) {
				LogOperationDialog lod = new LogOperationDialog();
				lod.show(getFragmentManager(), "");
			}
		};
	private OnClickListener cancelSearchButton = new OnClickListener(){
			@Override
			public void onClick(View me) {
				syncOperations(null);
			}
		};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		antistatic = this;
		setContentView(R.layout.activity_main);
		
		fundsAvailable = (TextView)findViewById(R.id.funds_available);
		
		newOperation = (Button)findViewById(R.id.add_operation);
		newOperation.setOnClickListener(logOperationButton);
		
		operationsList = (LinearLayout)findViewById(R.id.main_list);
		operations = Utils.load(this, null);
		syncOperations(null);
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
			Utils.shout("Feature is to be deleted");
			break;
		case (R.id.mm_export):
			export();
			break;
		case (R.id.mm_import):
			dbimport();
			break;
		case (R.id.mm_specialtags):
			Utils.shout("Use #save:<name> tag to transfer digits to your saving account\n"
			          + "#invest:<name> add real money to investment account"
			          + "#use:<name>:<price> when buying goods using before-saved money\n"
			          + "#kill:<name> to liquidate saving account and make it's savings available");
			break;
		case (R.id.mm_search):
			SearchDialog sd = new SearchDialog();
			sd.show(getFragmentManager(), "");
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

	public void search(String query){
		syncOperations(query);
	}
	
	public String lastUsedSyncArgument = null;
	public void syncOperations(String tagquery){
		lastUsedSyncArgument = tagquery;
		if (tagquery == null){
			newOperation.setText("Log Operation");
			newOperation.setOnClickListener(logOperationButton);;
		} else {
			newOperation.setText("Cancel Search");
			newOperation.setOnClickListener(cancelSearchButton);
		}
		
		//Render operation history
		operationsList.removeAllViews();
		accounts.clear();
		
		int searchBalance = 0;
		
		for (int i = operations.size() - 1; i >= 0; --i){
			if (tagquery == null || Utils.containsPartial(operations.get(i).tags, tagquery)){
				operationsList.addView(new OperationView(this, operations.get(i)));
				searchBalance += operations.get(i).delta;
			}
		}

		//Process savings balance
		int balance = 0, total = 0;;
		SavingAccount greedy;
		
		for (Operation horsey: operations){
			Operation.OTDContainer otd = horsey.getType();
			switch(otd.type){
			case DEFAULT:
				balance += horsey.delta;
				total += horsey.delta;
				break;
			case SAVE:
				greedy = Utils.findAccount(accounts, otd.accountname);
				if (greedy == null){
					greedy = new SavingAccount(otd.accountname);
					accounts.add(greedy);
				}
				greedy.balance -= horsey.delta; //add operation delta to saving account
				balance += horsey.delta;        //remove operation delta from available funds
				break;
			case INVEST:
				greedy = Utils.findAccount(accounts, otd.accountname);
				if (greedy == null){
					greedy = new SavingAccount(otd.accountname);
					accounts.add(greedy);
				}
				greedy.balance -= horsey.delta; //add operation delta to saving account
				balance += horsey.delta;
				total += horsey.delta;
				break;
			case USE:
				greedy = Utils.findAccount(accounts, otd.accountname);
				if (greedy != null){
					balance += greedy.balance - otd.value; //add fred funds to available
					total -= otd.value;                    //remove spent money from card
					accounts.remove(greedy);
				}
				break;
			case KILL:
				greedy = Utils.findAccount(accounts, otd.accountname);
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
		if (tagquery != null){
			sb.append('\n');
			sb.append('\n');
			sb.append("Query balance: ");
			sb.append(searchBalance);
		}
		fundsAvailable.setText(sb.toString());
	}
	
	public void add(Operation operation){
		operations.add(operation);
		syncOperations(null);
		Utils.save(this, operations, null);
	}
	
	public void replace(Operation source, Operation noperation){
		int knife = operations.indexOf(source);
		operations.remove(source);
		operations.add(knife, noperation);
		syncOperations(lastUsedSyncArgument);
		Utils.save(this, operations, null);
	}
	
	public void remove(Operation operation){
		operations.remove(operation);
		syncOperations(null);
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
		
		Utils.save(this, operations, "/stoarge/sdcard0/backup.sbl");
	}
	
	public void dbimport(){
		operations = Utils.load(this, "/stoarge/sdcard0/backup.sbl");
		syncOperations(null);
	}
}
