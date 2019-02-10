package com.milesseventh.finances;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

public class Utils {
	private static String ACTIVE_MOTH_DB = "moths";
	public static final int IMPORT_REQUEST = 77279;
	
	public static void saveMoth(Context ctxt, ArrayList<Moth> data, File base){
		try {
			//FileOutputStream fos = ctxt.openFileOutput(base == null ? ACTIVE_DB : base, Context.MODE_PRIVATE);
			FileOutputStream fos;
			if (base == null){
				fos = ctxt.openFileOutput(ACTIVE_MOTH_DB, Context.MODE_PRIVATE);
			} else {
				base.delete();
				base.createNewFile();
				fos = new FileOutputStream(base);//ctxt.openFileInput(new File(base));
			}
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(data);
			oos.close();
			fos.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<Moth> loadMoth(Context ctxt, String base){
		ArrayList<Moth> r;
		try{
			FileInputStream fos;
			if (base == null){
				fos = ctxt.openFileInput(ACTIVE_MOTH_DB);
			} else {
				fos = new FileInputStream(new File(base));//ctxt.openFileInput(new File(base));
			}
			ObjectInputStream oos = new ObjectInputStream(fos);
			r = (ArrayList<Moth>)oos.readObject();
			oos.close();
			fos.close();
			return r;
		} catch (FileNotFoundException fnfex){
			return new ArrayList<Moth>();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public static void copy(Context ctxt, String text){
		((ClipboardManager) ctxt.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText(ctxt.getPackageName(), text));
	}
	
	public static void shout(final String text){
		MainActivity.antistatic.runOnUiThread(new Runnable(){
			@Override
			public void run() {
				Toast.makeText(MainActivity.antistatic, text, Toast.LENGTH_LONG).show();
			}
		});
	}
	
	public static String getMonthName(int m){
		switch(m){
		case 0:
			return "January";
		case 1:
			return "February";
		case 2:
			return "March";
		case 3:
			return "Avril";
		case 4:
			return "May";
		case 5:
			return "June";
		case 6:
			return "July";
		case 7:
			return "August";
		case 8:
			return "September";
		case 9:
			return "October";
		case 10:
			return "November";
		case 11:
			return "December";
		default:
			return "wat";
		}
	}
}
