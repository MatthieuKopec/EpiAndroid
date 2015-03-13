package com.epiandroid.epitech.epiandroid;

import  android.app.Activity;

import  android.view.LayoutInflater;

import  android.view.View;
import  android.view.ViewGroup;

import  android.widget.ArrayAdapter;
import  android.widget.ImageView;
import  android.widget.TextView;

import  java.util.ArrayList;
import  java.util.HashMap;

import  android.util.Pair;

public class                 TrombiAdapter extends ArrayAdapter {
    private final TrombiActivity context;
    private final ArrayList<Pair<String, String>> data;

    @Override
    public View              getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = this.context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.trombi_row, null, true);
        Pair<String, String> tmp = data.get(position);

        ((TextView) rowView.findViewById(R.id.trombi_name)).setText(tmp.first);
        if (tmp.second != null) {
            ((ImageView) rowView.findViewById(R.id.trombi_image)).setImageResource(R.drawable.logo);
            if (!this.context.buildImage(tmp.second, (ImageView) rowView.findViewById(R.id.trombi_image), -1, -1, new HashMap<String, String>() {{ put("ignoreSSL", "true"); }}))
                this.context.flash(this.context.getString(R.string.api_internal_error_image), false);
        }
        else
            ((ImageView) rowView.findViewById(R.id.trombi_image)).setImageResource(R.drawable.logo);

        return (rowView);
    }

    public                   TrombiAdapter(TrombiActivity context, ArrayList<Pair<String, String>> dt) {
        super(context, R.layout.trombi_row, dt);

        this.context = context;
        this.data = dt;
    }
}
