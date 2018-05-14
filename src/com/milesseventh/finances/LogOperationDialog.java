package com.milesseventh.finances;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LogOperationDialog extends DialogFragment {
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		Builder builder = new Builder(getActivity());

		View inflated = getActivity().getLayoutInflater().inflate(R.layout.add_operation, null);
		final EditText d = (EditText)inflated.findViewById(R.id.ao_delta),
		               c = (EditText)inflated.findViewById(R.id.ao_comment),
		               t = (EditText)inflated.findViewById(R.id.ao_tags);
		builder.setView(inflated).setTitle("Log new operation").setNegativeButton("Cancel", new OnClickListener(){
			@Override
			public void onClick(DialogInterface unused, int unusedtoo) {
				
			}
		}).setPositiveButton("Submit", new OnClickListener(){
			@Override
			public void onClick(DialogInterface unused, int unusedtoo) {
				if (!d.getText().toString().trim().isEmpty())
					MainActivity.antistatic.add(
						new Operation(
							Integer.parseInt(d.getText().toString()), 
							c.getText().toString(), 
							t.getText().toString()
						)
					);
			}
		});

		return builder.create();
	}
}
