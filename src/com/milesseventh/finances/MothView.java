package com.milesseventh.finances;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class MothView extends LinearLayout {
	public Moth data;
	private final LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	
	public MothView(Context context, Moth nd) {
		super(context);
		setOrientation(LinearLayout.VERTICAL);
		setPadding(7, 7, 7, 7);
		setLayoutParams(lp);
		data = nd;
		
		Button bEdit = new Button(context);
		bEdit.setLayoutParams(lp);
		bEdit.setText(data.name);
		bEdit.setTextColor(Color.rgb(32, 184, 255));
		bEdit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				MainActivity.antistatic.openMothEditor(data);
			}
		});
		addView(bEdit);
		
		ProgressBar pb = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
		pb.setIndeterminate(false);
		pb.setLayoutParams(lp);
		pb.setMax(100);
		pb.setProgress((int)Math.round(data.getEfficiency() * 100f));
		pb.setPadding(0, 0, 0, 10);
		
		addView(pb);
	}
}
