package com.milesseventh.finances;

import java.io.Serializable;
import java.util.ArrayList;

public class Operation implements Serializable {
	private static final long serialVersionUID = -3382758464165988286L;
	
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
}
