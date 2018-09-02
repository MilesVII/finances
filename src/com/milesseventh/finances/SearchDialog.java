package com.milesseventh.finances;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

public class SearchDialog extends DialogFragment {
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		Builder builder = new Builder(getActivity());
		
		final AutoCompleteTextView sq = new AutoCompleteTextView(getActivity());
		sq.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, MainActivity.antistatic.getListOfAllUsedTags()));
		
		builder.setView(sq).setTitle("Search query").setNegativeButton("Cancel", new OnClickListener(){
			@Override
			public void onClick(DialogInterface unused, int unusedtoo) {}
		}).setPositiveButton("Search", new OnClickListener(){
			@Override
			public void onClick(DialogInterface unused, int unusedtoo) {
				String q = sq.getText().toString();
				if (!q.isEmpty())
					MainActivity.antistatic.search(q);
			}
		});
		return builder.create();
	}
}
