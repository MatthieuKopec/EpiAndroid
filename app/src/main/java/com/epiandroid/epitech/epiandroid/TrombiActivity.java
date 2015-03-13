package com.epiandroid.epitech.epiandroid;

import  android.os.Bundle;

import  android.widget.ListAdapter;
import  android.widget.ListView;
import  android.widget.SimpleAdapter;

import  com.google.gson.JsonElement;
import  com.google.gson.JsonObject;

import  android.util.Pair;

import  java.text.SimpleDateFormat;

import  java.util.ArrayList;
import  java.util.Calendar;
import  java.util.HashMap;
import  java.util.Map;

public class        TrombiActivity extends MainActivity {
    @Override
    protected void  onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.trombi_activity);
        this.showMenu();

        final JsonObject user = this.findUser();
        Map<String, String> params = new HashMap<String, String>() {{
            put("year", String.valueOf(Integer.valueOf(new SimpleDateFormat("yyyy").format(Calendar.getInstance().getTime())) - user.get("studentyear").getAsInt()));
            put("location", user.get("location").getAsString());
        }};
        if (!this.buildRequest("trombi", "GET", params, null))
            this.flash(getString(R.string.api_internal_error), false);
    }

    @Override
    protected void  onApiCompleted(String identity, Pair<Integer, String> error, JsonObject result) {
        if (error != null) {
            String msg;

            switch (error.second) {
                default:
                    msg = getString(R.string.api_unknown_error);
                    break;
            }

            this.flash(msg, false);
            return ;
        }

        if (!result.has("items")) {
            this.flash(getString(R.string.api_invalid_data), false);
            return;
        }

        ArrayList<Pair<String, String>> trombi = new ArrayList<>();

        for (JsonElement tmp : result.getAsJsonArray("items")) {
            final JsonObject tb = tmp.getAsJsonObject();

            trombi.add(new Pair<>((!tb.get("title").isJsonNull() ? tb.get("title").getAsString() : getString(R.string.unknown_m)) + (!tb.get("login").isJsonNull() ? " (" +  tb.get("login").getAsString() + ")" : ""),
                                  (!tb.get("picture").isJsonNull() ? tb.get("picture").getAsString() : null)));
        }
        TrombiAdapter adapter = new TrombiAdapter(this, trombi);
        ((ListView) findViewById(R.id.trombi)).setAdapter(adapter);
    }

    public          TrombiActivity() {
        super();
    }
}
