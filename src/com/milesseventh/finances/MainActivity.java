package com.milesseventh.finances;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

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
	private LinearLayout viewList;
	private ArrayList<Moth> moths = new ArrayList<Moth>();
	
	private Button newOperation;
	private TextView header;
	public static MainActivity antistatic;
	private OnClickListener logOperationButton = new OnClickListener(){
			@Override
			public void onClick(View unused) {
				Moth m = new Moth();
				Calendar c = Calendar.getInstance();
				m.name = Utils.getMonthName(c.get(Calendar.MONTH)) + " " + (1900 +  c.get(Calendar.YEAR));
				openMothEditor(m);
			}
		};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		antistatic = this;
		setContentView(R.layout.activity_main);
		
		header = (TextView)findViewById(R.id.funds_available);
		
		newOperation = (Button)findViewById(R.id.add_operation);
		newOperation.setOnClickListener(logOperationButton);
		
		viewList = (LinearLayout)findViewById(R.id.main_list);
		//operations = Utils.load(this, null);
		moths = Utils.loadMoth(this, null);
		sync();
		
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
			Utils.shout("Feature was meant to be deleted, but it is the only feature available");
			break;
		case (R.id.mm_plot):
			break;
		case (R.id.mm_export):
			export();
			break;
		case (R.id.mm_import):
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.addCategory(Intent.CATEGORY_DEFAULT);
			intent.setType("file/*");
			startActivityForResult(intent, Utils.IMPORT_REQUEST);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public String lastUsedSyncArgument = null;
	public void sync(){
		ArrayList<Delta> sumLoans = new ArrayList<Delta>();
		for (Moth m: moths){
			m.calculate();
			
			for (Delta scanned: m.spentOnLoans){
				Delta existingEntry = null;
				
				//Search for previous loans
				for (Delta b: sumLoans)
					if (b.comment.equals(scanned.comment)){
						existingEntry = b;
						break;
					}
				
				if (existingEntry == null){
					//Log new borrower
					sumLoans.add(new Delta(scanned));
				} else {
					//Increase loan size of entry
					existingEntry.delta += scanned.delta;
				}
			}
		}
		
		//Generate loans report
		String report = "";
		for (Delta entry: sumLoans)
			if (entry.delta != 0)
				report += entry.comment + ": " + entry.delta + "\n";
		header.setText(report.trim());

		viewList.removeAllViews();
		for (int i = moths.size() - 1; i >= 0; --i){
			viewList.addView(new MothView(this, moths.get(i)));
		}
		
		Utils.saveMoth(this, moths, null);
	}

	public void export(){
		File target = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "backup.sbx");
		Utils.shout(target.getAbsolutePath());
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if (requestCode == Utils.IMPORT_REQUEST && resultCode == RESULT_OK){
			Utils.shout("Imported successfully: " + data.getData().getPath());
			dbimport(data.getData().getPath()/*data.getDataString()*/);
		}
	}
	
	public void dbimport(String filename){
		moths = Utils.loadMoth(this, filename);
		sync();
	}
	
	public void openMothEditor(Moth data){
		LogMothDialog lmd = new LogMothDialog();
		lmd.moth = data;
		lmd.show(getFragmentManager(), "");
	}
	
	public boolean mothRegistered(Moth m){
		return moths.contains(m);
	}
	
	public void registerMoth(Moth m){
		if (!mothRegistered(m)){
			moths.add(m);
		}
	}
	
	public Moth requestPreviousMoth(Moth m){
		int i = moths.indexOf(m);
		
		if (i > 0)
			return moths.get(i - 1);
		else
			return null;
	}
}
