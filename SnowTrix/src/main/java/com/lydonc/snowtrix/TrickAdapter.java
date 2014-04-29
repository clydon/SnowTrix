package com.lydonc.snowtrix;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class TrickAdapter extends ArrayAdapter<Trick> {
    private Context mContext;
    private List<Trick> mTricks;

    public TrickAdapter(Context context, List<Trick> objects) {
        super(context, R.layout.trick_row_item, objects);
        this.mContext = context;
        this.mTricks = objects;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView == null){
            LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
            convertView = mLayoutInflater.inflate(R.layout.trick_row_item, null);
        }

        Trick trick = mTricks.get(position);

        TextView trickNameView = (TextView) convertView.findViewById(R.id.trick_name);
        trickNameView.setText(trick.getTrickName());

        return convertView;
    }
}