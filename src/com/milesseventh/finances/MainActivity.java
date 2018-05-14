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
		}
		return super.onOptionsItemSelected(item);
	}

	public void syncOperations(){
		int balance = 0;
		operationsList.removeAllViews();
		for (Operation whiteops: operations){
			operationsList.addView(new OperationView(this, whiteops));
			balance += whiteops.delta;
		}
		fundsAvailable.setText("Available: " + balance);
	}
	
	public void add(Operation operation){
		operations.add(operation);
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
