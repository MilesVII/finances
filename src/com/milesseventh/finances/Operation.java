package com.milesseventh.finances;

import java.io.Serializable;
import java.util.ArrayList;

public class Operation implements Serializable {
	private static final long serialVersionUID = -3382758464165988286L;
	
	public enum Type {
		DEFAULT, SAVE, USE, KILL
	};
	public static final int STID_SAVING = 0,
	                        STID_USE = 1,
	                        STID_KILL = 2,
	                        STID_TRANSFER = 3;
	public static final String[] SPECIAL_TAGS = {
		"save",   //save:accountname
		"use",    // use:accountname:transaction
		"kill",   //kill:accountname
		"trans"  //trans:from:to
	};
	
	public int delta;
	public String comment;
	public ArrayList<String> tags = new ArrayList<String>();
	//public Instant timeStamp;
	
	public Operation(int ndelta, String rawComment, String[] intags) {
		delta = ndelta;
		comment = rawComment.replace("r", "").replace("\n", "\\").trim();
		//for (String rawTag: rawTags.split("\n"))
		//	tags.add(Utils.sanitizeTag(rawTag));
		for (String tag: intags)
			tags.add(tag);
	}
	
	public void checkTagSetTypeValidity(){
		checkTagSetTypeValidity(tags);//TODO
	}
	
	public static boolean checkTagSetTypeValidity(ArrayList<String> tags){
		boolean deftype = true;
		for (String tag: tags){
			for (String st: SPECIAL_TAGS)
				if (tag.startsWith(st + ":")){
					if (!deftype)
						return false;
					deftype = false;
				}
		}
		return true;
	}
	
	public String tagarg1, tagarg2;
	public int tagargnum;
	public Type getType(){
		for (String tag: tags){
			if (tag.startsWith(SPECIAL_TAGS[STID_SAVING]) &&
			    tag.split(":").length >= 2){
				tagarg1 = tag.split(":")[1];
				return Type.SAVE;
			}
			if (tag.startsWith(SPECIAL_TAGS[STID_USE]) &&
			    tag.split(":").length >= 3 && 
			    Utils.isParseable(tag.split(":")[2])){
				tagarg1 = tag.split(":")[1];
				tagargnum = Integer.parseInt(tag.split(":")[2]);
				return Type.USE;
			}
			if (tag.startsWith(SPECIAL_TAGS[STID_KILL]) &&
			    tag.split(":").length >= 2){
				tagarg1 = tag.split(":")[1];
				return Type.KILL;
			}
		}
		return Type.DEFAULT;
	}
}
