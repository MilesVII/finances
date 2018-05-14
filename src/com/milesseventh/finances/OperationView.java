package com.milesseventh.finances;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OperationView extends LinearLayout {
	public Operation data;
	private TextView title, tags;
	private Button bEdit, bDelete;
	private final LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	
	public OperationView(Context context, Operation nd) {
		super(context);
		setOrientation(LinearLayout.VERTICAL);
		setPadding(7, 7, 7, 7);
		setLayoutParams(lp);
		data = nd;
		
		title = new TextView(context);
		title.setText("" + data.delta + ": " + data.comment);
		title.setTextColor(data.delta > 0 ? Color.rgb(32, 184, 255) : Color.rgb(218, 64, 0));
		addView(title);
		
		tags = new TextView(context);
		StringBuilder sb = new StringBuilder();
		for (String tag: data.tags){
			sb.append('#');
			sb.append(tag);
			sb.append(' ');
		}
		tags.setText(sb);
		addView(tags);
		
		LinearLayout controls = new LinearLayout(context);
		controls.setLayoutParams(lp);
		controls.setOrientation(LinearLayout.HORIZONTAL);
		bEdit = new Button(context);
		bEdit.setText("Edit");
		controls.addView(bEdit);
		bDelete = new Button(context);
		bDelete.setText("Remove");
		bDelete.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				MainActivity.antistatic.remove(data);
			}
		});
		controls.addView(bDelete);
		addView(controls);
	}
}
