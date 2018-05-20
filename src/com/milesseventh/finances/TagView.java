package com.milesseventh.finances;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TagView extends LinearLayout {
	public String tag;
	private TextView title;
	private Button bDelete;
	private final LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	
	public TagView(Context context, String t) {
		super(context);
		setOrientation(LinearLayout.HORIZONTAL);
		setPadding(7, 7, 7, 7);
		setLayoutParams(lp);
		setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
		
		tag = Utils.sanitizeTag(t);
		
		title = new TextView(context);
		title.setText(tag);
		addView(title);
		
		bDelete = new Button(context);
		bDelete.setText("X");
		bDelete.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		final View target = this;
		bDelete.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View victim) {
				((LinearLayout)target.getParent()).removeView(target);
			}
		});
		addView(bDelete);
	}
}
