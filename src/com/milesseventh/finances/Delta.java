package com.milesseventh.finances;

import java.io.Serializable;

public class Delta implements Serializable{
	private static final long serialVersionUID = 4617289939669768479L;
	public String comment;
	public int delta;
	
	public Delta(Delta clone){
		comment = String.copyValueOf(clone.comment.toCharArray());
		delta = clone.delta;
	}
	
	public Delta(String nborrower, int ndelta) {
		delta = ndelta;
		comment = nborrower;
	}
}
