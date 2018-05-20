package com.milesseventh.finances;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class LogOperationDialog extends DialogFragment {
	public Operation editSource = null;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		Builder builder = new Builder(getActivity());

		View inflated = getActivity().getLayoutInflater().inflate(R.layout.add_operation, null);
		final EditText d = (EditText)inflated.findViewById(R.id.ao_delta),
		               c = (EditText)inflated.findViewById(R.id.ao_comment);
		
		final AutoCompleteTextView ti = (AutoCompleteTextView)inflated.findViewById(R.id.ao_taginput);
		final Button at = (Button)inflated.findViewById(R.id.ao_addtag);
		final LinearLayout tl = (LinearLayout)inflated.findViewById(R.id.ao_tags);
		
		ti.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, MainActivity.antistatic.getListOfAllUsedTags()));
		
		at.setOnClickListener(new android.view.View.OnClickListener(){
			@Override
			public void onClick(View unused) {
				if (!Utils.sanitizeTag(ti.getText().toString()).isEmpty())
					tl.addView(new TagView(getActivity(), ti.getText().toString()));
				ti.setText("");
			}
		});
		
		
		if (editSource != null){
			d.setText("" + editSource.delta);
			c.setText(editSource.comment);
			for (String tag: editSource.tags)
				tl.addView(new TagView(getActivity(), tag));
		}
		
		
		builder.setView(inflated).setTitle("Log new operation").setNegativeButton("Cancel", new OnClickListener(){
			@Override
			public void onClick(DialogInterface unused, int unusedtoo) {
				
			}
		}).setPositiveButton("Submit", new OnClickListener(){
			@Override
			public void onClick(DialogInterface unused, int unusedtoo) {
				if (!d.getText().toString().trim().isEmpty()){
					String[] t = new String[tl.getChildCount()];
					for (int i = 0; i < t.length; ++i)
						t[i] = ((TagView)tl.getChildAt(i)).tag;
					
					if (editSource == null)
						MainActivity.antistatic.add(
							new Operation(
								Integer.parseInt(d.getText().toString()), 
								c.getText().toString(), 
								t
							)
						);
					else
						MainActivity.antistatic.replace(editSource,
								new Operation(
									Integer.parseInt(d.getText().toString()), 
									c.getText().toString(), 
									t
								)
							);
				}
			}
		});

		return builder.create();
	}
	
}
