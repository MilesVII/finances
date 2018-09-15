package com.milesseventh.finances;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Operation implements Serializable {
	private static final long serialVersionUID = -3382758464165988286L;
	public class OTDContainer{
		String accountname = null, from = null, to = null;
		int value = -1, altvalue = -1;
		Type type = Type.DEFAULT;
	}
	
	public enum Type {
		DEFAULT, SAVE, USE, KILL, INVEST
	};
	public static final int STID_SAVING = 0,
	                        STID_USE = 1,
	                        STID_KILL = 2,
	                        STID_INVEST = 3,
	                        STID_LOAN = 4,
	                        STID_TRANSFER = 5;
	public static final String[] SPECIAL_TAGS = {
		"save",    //save:accountname
		"use",     //use:accountname:value
		"kill",    //kill:accountname
		"invest",  //invest:accountname
		"loan",  //invest:accountname
		"trans"    //trans:from:to
	};
	
	public int delta;
	public String comment;
	public ArrayList<String> tags = new ArrayList<String>();
	public Date timeStamp;
	
	public Operation(int ndelta, String rawComment, String[] intags){
		this(ndelta, rawComment, intags, Calendar.getInstance().getTime());
	}
	
	public Operation(int ndelta, String rawComment, String[] intags, Date ntimeStamp){
		delta = ndelta;
		comment = rawComment.replace("\r", "").replace("\n", "\\").trim();
		//for (String rawTag: rawTags.split("\n"))
		//	tags.add(Utils.sanitizeTag(rawTag));
		for (String tag: intags)
			tags.add(tag);
		timeStamp = ntimeStamp;
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
	
	public OTDContainer getType(){
		OTDContainer r = new OTDContainer();
		
		for (String tag: tags){
			if (tag.startsWith(SPECIAL_TAGS[STID_SAVING]) &&
			    tag.split(":").length >= 2){
				r.accountname = r.to = tag.split(":")[1];
				r.type = Type.SAVE;
				return r;
			}
			if (tag.startsWith(SPECIAL_TAGS[STID_USE]) &&
			    tag.split(":").length >= 3 && 
			    Utils.isParseable(tag.split(":")[2])){
				r.accountname = tag.split(":")[1];
				r.value = Integer.parseInt(tag.split(":")[2]);
				r.type = Type.USE;
				return r;
			}
			if (tag.startsWith(SPECIAL_TAGS[STID_KILL]) &&
			    tag.split(":").length >= 2){
				r.accountname = tag.split(":")[1];
				r.type = Type.KILL;
				return r;
			}
			if ((tag.startsWith(SPECIAL_TAGS[STID_INVEST]) || 
			    tag.startsWith(SPECIAL_TAGS[STID_LOAN])) &&
			    tag.split(":").length >= 2){
				r.accountname = tag.split(":")[1];
				r.type = Type.INVEST;
				return r;
			}
		}
		return r;
	}
}
