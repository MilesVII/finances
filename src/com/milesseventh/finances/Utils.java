package com.milesseventh.finances;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

public class Utils {
	private static String ACTIVE_DB = "active";
	
	/*
	 * Use null as database name to save active base
	 */
	public static void save(Context ctxt, ArrayList<Operation> data, String base){
		try {
			FileOutputStream fos = ctxt.openFileOutput(base == null ? ACTIVE_DB : base, Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(data);
			oos.close();
			fos.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	/*
	 * Use null as database name to load active base
	 */
	public static ArrayList<Operation> load(Context ctxt, String base){
		ArrayList<Operation> r;
		try{
			FileInputStream fos = ctxt.openFileInput(base == null ? ACTIVE_DB : base);
			ObjectInputStream oos = new ObjectInputStream(fos);
			r = (ArrayList<Operation>)oos.readObject();
			oos.close();
			fos.close();
			return r;
		} catch (FileNotFoundException fnfex){
			return new ArrayList<Operation>();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static void copy(Context ctxt, String txt){
		((ClipboardManager) ctxt.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText(ctxt.getPackageName(), txt));
	}
}
