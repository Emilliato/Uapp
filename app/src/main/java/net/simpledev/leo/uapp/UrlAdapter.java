package net.simpledev.leo.uapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by LEoe on 8/15/2018.
 */

public class UrlAdapter extends ArrayAdapter<MyUrl> {
	
	public UrlAdapter(@NonNull Context context,List<MyUrl> urlLst) {
		super(context, 0,urlLst);
	}
	
	@NonNull
	@Override
	public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
		MyUrl url = getItem(position);
		View currentView =convertView;
		if (currentView==null){
			currentView = LayoutInflater.from(getContext()).inflate(R.layout.list_item,parent,false);
		}
		TextView textView = currentView.findViewById(R.id.tvListItem);
		textView.setText(url.toString());
		return currentView;
	}
}
