package com.milesseventh.finances;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import com.jjoe64.graphview.series.DataPoint;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
	public ArrayList<Account> accounts = new ArrayList<Account>();
	public static MainActivity antistatic;
	public static DataPoint[] plotData;
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
		
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
		    != PackageManager.PERMISSION_GRANTED) {
			Utils.shout("It says \"Go fuck yourself\"");
			ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 734);
		}
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
		case (R.id.mm_plot):
			PlotDialog pd = new PlotDialog();
			pd.show(getFragmentManager(), "");
			break;
		case (R.id.mm_export):
			export();
			break;
		case (R.id.mm_import):
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.addCategory(Intent.CATEGORY_DEFAULT);
			intent.setType("file/*");
			startActivityForResult(intent, Utils.IMPORT_REQUEST);
			//dbimport();
			break;
		case (R.id.mm_specialtags):
			Utils.shout("Use #save:<name> tag to reserve part of funds\n"
			          + "#use:<name>:<price> when buying goods using before-saved money\n"
			          + "#kill:<name> to liquidate saving-marked account and make it's savings available\n"
			          + "#invest:<name> or #loan:<borrower> add real money to investment-marked account\n");
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

		//Plotting init
		ArrayList<DataPoint> dps = new ArrayList<DataPoint>();
		int plottingPeriod = -1;
		Calendar tCalendar = Calendar.getInstance();
		
		//Process savings balance
		int balance = 0, total = 0;;
		Account greedy;
		
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
					greedy = new Account(otd.accountname, Account.Marker.SAVING);
					accounts.add(greedy);
				}
				greedy.balance -= horsey.delta; //add operation delta to saving account
				balance += horsey.delta;        //remove operation delta from available funds
				break;
			case INVEST:
				greedy = Utils.findAccount(accounts, otd.accountname);
				if (greedy == null){
					greedy = new Account(otd.accountname, Account.Marker.RETURNABLE);
					accounts.add(greedy);
				}
				greedy.balance -= horsey.delta; //add operation delta to saving account
				if (greedy.balance == 0)
					accounts.remove(greedy);
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
			
			//Plotting DP update
			tCalendar.setTime(horsey.timeStamp);
			int currentPeriod = tCalendar.get(Calendar.WEEK_OF_YEAR);
			if (currentPeriod != plottingPeriod){
				if (plottingPeriod == -1){
					plottingPeriod = currentPeriod;
					continue;
				}
				int invs = 0;
				for (Account acc: accounts){
					if (acc.marker == Account.Marker.RETURNABLE)
						invs += acc.balance;
				}
				dps.add(new DataPoint(plottingPeriod, total + invs));
				//Yearshift //Or we just traveled back in time //Or operations order is just fucked up
				if (plottingPeriod > currentPeriod){
					//TODO: Aaaaa don't wanna do anything here but I have to
				}
				//Add points in gaps if operations not present
				for (int i = plottingPeriod + 1; i < currentPeriod; ++i)
					dps.add(new DataPoint(i, total + invs));
				plottingPeriod = currentPeriod;
			}
		}
		
		//Saving plot data
		plotData = dps.toArray(new DataPoint[dps.size()]);
		
		////////////////////////////////////
		StringBuilder sb = new StringBuilder("Available: ");
		sb.append(balance);
		sb.append(" (");
		sb.append(total);
		sb.append(" on card)\nAccounts:");
		for (Account sa: accounts){
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
		/*StringBuilder sb = new StringBuilder();
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
		Utils.copy(this, sb.toString());*/
		File target = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "backup.sbl");
		Utils.shout(target.getAbsolutePath());
		Utils.save(this, operations, target);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if (requestCode == Utils.IMPORT_REQUEST && resultCode == RESULT_OK){
			Utils.shout(data.getData().getPath());
			dbimport(data.getData().getPath()/*data.getDataString()*/);
		}
	}
	
	public void dbimport(String filename){
		operations = Utils.load(this, filename);
		syncOperations(null);
	}
}
